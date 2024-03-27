package com.cyclemost.powerpurge;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * Holds the config values
 * 
 * @author dbridges
 */
public class PathConfig {
  
  private static final Gson GSON = new Gson();
  
  private String name;
  private String filePattern;
  private boolean recursive;
  private long fileAgeDays;
  private long archiveAgeDays;
  List<String> paths;
  
  /**
   * Creates an empty config.
   */
  public PathConfig() {
  }
  
  @Override
  public String toString() {
    return String.format("%s (filePattern: %s, recursive: %s, fileAgeDays: %s, archiveAgeDays: %s)",
      getName(),
      getFilePattern(),
      isRecursive(),
      getFileAgeDays(),
      getArchiveAgeDays());
  }
  
  public boolean isFilePurgeEnabled() {
    return fileAgeDays > 0;
  }
  
  /**
   * Returns a flag indicating whether purged files in this folder
   * will be archived. 
   * 
   * @return 
   */
  public boolean isFileArchiveEnabled() {
    return archiveAgeDays > 0;
  }
  
  public List<String> getConfigWarnings() {
    List<String> warnings = new ArrayList<>();
    if (fileAgeDays < 1) {
      warnings.add("fileAgeDays not defined");
    }

    return warnings;
  }
  
  public static PathConfig fromJson(String json) {
    return GSON.fromJson(json, PathConfig.class);
  }
  
  public String toJson() {
    return GSON.toJson(this);
  }

  public PathConfig createClone() {
    return fromJson(toJson());
  } 
  
  public static List<PathConfig> fromFile(String filePath) throws Exception {
    try {
      String json = FileUtils.readFileToString(new File(filePath), Charset.defaultCharset());
      Type listType = new TypeToken<List<PathConfig>>(){}.getType();
      return GSON.fromJson(json, listType);
    }
    catch (Exception ex) {
      throw new Exception("Error loading config", ex);
    }
  }
  
  //// get/set methods

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFilePattern() {
    return filePattern;
  }

  public void setFilePattern(String filePattern) {
    this.filePattern = filePattern;
  }

  public boolean isRecursive() {
    return recursive;
  }

  public void setRecursive(boolean recursive) {
    this.recursive = recursive;
  }

  public long getFileAgeDays() {
    return fileAgeDays;
  }

  public void setFileAgeDays(long fileAgeDays) {
    this.fileAgeDays = fileAgeDays;
  }

  public long getArchiveAgeDays() {
    return archiveAgeDays;
  }

  public void setArchiveAgeDays(long archiveAgeDays) {
    this.archiveAgeDays = archiveAgeDays;
  }  
  
  public List<String> getPaths() {
    if (paths == null) {
      paths = new ArrayList<>();
    }
    return paths;
  }
}
