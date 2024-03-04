package com.cyclemost.powerpurge;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main logic for processing directories.
 * 
 * @author dbridges
 */
public class PowerPurgeProcessor {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PowerPurgeProcessor.class);
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HHmmss"); 

  boolean reportOnly;
  boolean pruneArchive;
  
  int archiveTotalCount = 0;
  int deleteTotalCount = 0;
  long grandTotalSize = 0;
  int totalPathsProcessed = 0;
    
  public PowerPurgeProcessor() {
  }
  
  /**
   * Processes the specified path. The entire directory tree under the
   * specified path will be processed, recursively.
   * 
   * @param config purge options
   */
  public void process(PathConfig config) {
    
    LOGGER.info("Config: {}", config);
    for (String path : config.getPaths()) {
      try {
        performActions(path, config);
      }
      catch (Exception ex) {
        LOGGER.error("Error processing path {}", path, ex);
      }
    }    
  }
  
  /**
   * Performs actions specified by the active config file for this path.
   * 
   * @param path
   * @param config 
   */
  void performActions(String path, PathConfig config) throws IOException {
    
    String[] level = StringUtils.split(path, "/");
    if (level.length < 2) {
      // Safety check
      LOGGER.error("Cannot purge root or top-level folder {}", path);
      return;
    }
    
    LOGGER.info("Processing: {}", path);
    ++totalPathsProcessed;
        
    int archiveCount = 0;
    int deleteCount = 0;
    long totalSize = 0;
    
    String filters[] = StringUtils.split(config.getFilePattern(), ";");
    FileFilter fileFilter = new WildcardFileFilter(filters);
        
    // Get list of files to be purged
    List<File> filesToPurge = new ArrayList<>();
    List<File> archivesToPurge = new ArrayList<>();
    var files = Paths.get(path).toFile().listFiles();
    if (files == null) {
      return;
    }
    for (var file : files) {
      if (file.isHidden()) {
        // TODO: Make this an option?
        continue;
      }
      if (config.isFileArchiveEnabled() && isArchiveFile(file)) {
        long fileAge = fileAgeDays(file);
        if (fileAge > config.getArchiveAgeDays()) {
          LOGGER.debug("Delete archive file {} ({} days old)", file.getName(), fileAge);
          archivesToPurge.add(file);
        }
        continue;
      }
      if (file.isFile() && fileNameMatch(file, fileFilter, config)) {
        // File pattern matches; check file age
        long fileAge = fileAgeDays(file);
        if (config.isFilePurgeEnabled() && fileAge > config.getFileAgeDays()) {
          LOGGER.debug("Delete file {} ({} days old)", file.getName(), fileAge);
          filesToPurge.add(file);
        }
      }
      if (file.isDirectory() && config.isRecursive()) {
        performActions(file.getPath(), config);
      }
    }

    for (File file : filesToPurge) {
      totalSize += FileUtils.sizeOf(file);
    }
    for (File file : archivesToPurge) {
      totalSize += FileUtils.sizeOf(file);
    }
    grandTotalSize += totalSize;
    
    if (reportOnly) {
      return;
    }
    
    // Archive files
    if ((!filesToPurge.isEmpty()) && config.isFileArchiveEnabled()) {
      String archiveName = String.format("archive-%s.zip", DATE_FORMAT.format(new Date()));
      File archivePath = Paths.get(path, archiveName).toFile();
      Map<String, String> env = new HashMap<>();
      env.put("create", String.valueOf(!archivePath.exists()));    
      try (FileSystem zipFileSystem = FileSystems.newFileSystem(archivePath.toPath(), env)) {
        for (File file : filesToPurge) {
          if (addFileToArchive(file, zipFileSystem)) {
            ++archiveCount;
            LOGGER.debug("Archived file: {}", file.getName());
          }
          else {
            // archive failed; do not delete file
            LOGGER.error("Archive failed for {}", file.getName());
          }
        }
      }
    }
    
    // Delete files
    filesToPurge.addAll(archivesToPurge);
    for (File file : filesToPurge) {
      try {
        Files.delete(file.toPath());
        ++deleteCount;
      }
      catch (IOException ex) {
        LOGGER.error("Error deleting {}", file, ex);
      }
    }
        
    LOGGER.info("Archived {} files, deleted {} files, {}",  
      archiveCount, deleteCount, FileUtils.byteCountToDisplaySize(totalSize));
    
    archiveTotalCount += archiveCount;
    deleteTotalCount += deleteCount;
  }
  
  /**
   * Returns true if this file appears to be an archive file.
   * 
   * @param file
   * @return 
   */
  private static boolean isArchiveFile(File file) {
    return !file.isHidden() && 
      file.getName().toLowerCase().startsWith("archive") &&
      file.getName().toLowerCase().endsWith(".zip");
  }
  
  /**
   * Returns true if all of the following are true.
   * <ul>
   * <li>File is not hidden</li>
   * <li>File name matches the config pattern </li>
   * </ul>
   * @param file
   * @param config
   * @return 
   */
  static boolean fileNameMatch(File file, FileFilter filter, PathConfig config) {
  
    boolean nameMatch = filter.accept(file);
    
    return nameMatch && 
           file.isFile() &&
           !file.isHidden();
  }
  
  /**
   * Adds the specified file to the zip archive.
   * 
   * @param file
   * @param zipFile
   * @return
   * @throws IOException 
   */
  static boolean addFileToArchive(File file, FileSystem zipFileSystem) throws IOException {
    try {
      Path pathInZipFile = zipFileSystem.getPath(file.getName());
      Files.copy(file.toPath(), pathInZipFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
      return true;
    }    
    catch (IOException ex) {
      LOGGER.error("Error archiving {}", file.getName(), ex);
      return false;
    }
  }
  
  /**
   * Returns the age of a file, in days.
   * @param file
   * @return
   * @throws IOException 
   */
  static long fileAgeDays(File file) throws IOException {
    FileTime fileTime = Files.getLastModifiedTime(file.toPath());
    Instant fileInstant = fileTime.toInstant();
    Instant now = new Date().toInstant();
    Duration difference = Duration.between(fileInstant, now);
    return difference.toDays();    
  }

  public boolean isReportOnly() {
    return reportOnly;
  }

  public void setReportOnly(boolean reportOnly) {
    this.reportOnly = reportOnly;
  }

  public int getArchiveTotalCount() {
    return archiveTotalCount;
  }

  public int getDeleteTotalCount() {
    return deleteTotalCount;
  }

  public long getGrandTotalSize() {
    return grandTotalSize;
  }

  public int getTotalPathsProcessed() {
    return totalPathsProcessed;
  }

  public boolean isPruneArchive() {
    return pruneArchive;
  }

  public void setPruneArchive(boolean pruneArchive) {
    this.pruneArchive = pruneArchive;
  }
  
}
