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
  public void testLoadConfig() throws Exception {
    List<PathConfig> configs = PathConfig.fromFile("/Users/dbridges/Desktop/purge-config.json");
    for (var config : configs) {
      System.out.println(config.toString());
    }
  }
  
  @Test
  public void testCommand() throws ParseException {
    //ShellTree.main(new String[] {});
    PowerPurge.main(new String[] {
      //"-report", 
      "-prune",
      "-path",
      "/mnt/projects"
      //"/Users/dbridges/CycleMost/shelltree/shelltree/src/main/resources/testroot"
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
  

