package com.cyclemost.powerpurge;

import java.io.File;
import java.io.FileFilter;
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
  

