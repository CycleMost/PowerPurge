// Copyright (c) 2023 CPO Commerce, LLC
//
package com.cyclemost.powerpurge;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dbridges
 */
public class ZipUtil {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtil.class);

  /**
   * Creates the specified zip file and adds the specified list of 
   * files to it. File dates are preserved in the zip file.
   * 
   * @param files
   * @param zipPath
   * @throws java.io.IOException
   */
  public static void zipFiles(List<File> files, String zipPath) throws IOException {

    try (FileOutputStream fos = new FileOutputStream(zipPath);
         ZipOutputStream zos = new ZipOutputStream(fos)) 
    {      
      for (File file : files) {
        String filePath = file.getPath();
        LOGGER.debug("Zipping {}", filePath);
        // for ZipEntry we need only the filename without path
        ZipEntry ze = new ZipEntry(file.getName());
        // Preserve file date
        ze.setLastModifiedTime(FileTime.fromMillis(file.lastModified()));
        zos.putNextEntry(ze);
        //read the file and write to ZipOutputStream
        try (FileInputStream fis = new FileInputStream(filePath)) {        
          fis.transferTo(zos);
          zos.closeEntry();
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error", e);
      throw e;
    }
  }

}
