package com.cyclemost.powerpurge;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author dbridges
 */
public class Tester {
 
  @Test
  @Ignore
  public void testZip() throws Exception {
    
    File dir = new File("/Users/dbridges/Desktop/Data/demandware");
    String archiveName = "/Users/dbridges/Desktop/Data/demandware/archive1.zip";
    
    List<File> filesToZip = new ArrayList<>();
    for (File file : dir.listFiles()) {
      if (file.isFile() && !file.isHidden()) {
        filesToZip.add(file);
      }
    }
    
    ZipUtil.zipFiles(filesToZip, archiveName);
  }
  
  @Test
  @Ignore
  public void testIsArchive() throws Exception {
    String[] fileNames = {
      "archivefile.dat",
      "archivefile.txt",
      "archivefile.zip",
      "archivefile.gz",
      "archivefile.gzip",
      "archivefile.data.zip",
      "archivefile.data.gz"
    };
    
    for (var name : fileNames) {
      System.out.println(String.format("%s -> %s", name, PowerPurgeProcessor.isArchiveFile(new File(name))));
    }
  }
  
  @Test
  @Ignore
  public void testLoadConfig() throws Exception {
    List<PathConfig> configs = PathConfig.fromFile("/Users/dbridges/Desktop/purge-config.json");
    for (var config : configs) {
      System.out.println(config.toString());
    }
  }
  
  @Test
  @Ignore
  public void testCommand() throws ParseException {
    PowerPurge.main(new String[] {
      //"-report", 
      //"-prune",
      "-config",
      "/Users/dbridges/Desktop/purge-config.json"
    });
  }
  
  @Test
  @Ignore
  public void testWildcards() {

    String fileNames[] = {"test.csv", "test.txt", "test.dat", "stuff.log"};
    
    String filters[] = {"*"};
    
    FileFilter fileFilter = new WildcardFileFilter(filters);

    for (var fileName : fileNames) {
      System.out.println(String.format("%s: %s", fileName, fileFilter.accept(new File(fileName))));
    }

  }
}
  

