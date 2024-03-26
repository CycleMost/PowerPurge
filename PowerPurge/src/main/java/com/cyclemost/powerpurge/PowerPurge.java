package com.cyclemost.powerpurge;

import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dbridges
 */
public class PowerPurge {

  private static final Logger LOGGER = LoggerFactory.getLogger(PowerPurge.class);

  private static final String CMD_CONFIG = "config";
  private static final String CMD_REPORT = "report";
  
  public static void main(String[] args) throws ParseException {

    // Create command line options
    Options options = new Options();
    
    Option processPathOption = Option.builder(CMD_CONFIG)
                         .argName("config-file [config-file]...")
                         .hasArgs()
                         .desc("config file(s) to process")
                         .build();    
    options.addOption(processPathOption);
    
    Option reportOnlyOption = Option.builder(CMD_REPORT)
                         .desc("run in report only mode")
                         .build();    
    options.addOption(reportOnlyOption);
    
    //parse the options passed as command line arguments
    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);

      boolean reportOnly = cmd.hasOption(CMD_REPORT);

      if (cmd.hasOption(CMD_CONFIG)) {
        String paths[] = cmd.getOptionValues(CMD_CONFIG);
        run(paths, reportOnly);
      } else {
        printHelp(options);
      }
    }
    catch (UnrecognizedOptionException ex) {
      LOGGER.error(ex.getMessage());
    }
    catch (Throwable t) {
      LOGGER.error("Error", t);
    }
  }

  private static void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.setWidth(160);
    formatter.printHelp(".", options);
  }
  
  private static void run(String[] configPaths, boolean reportOnly) {
    if (configPaths == null || configPaths.length == 0) {
      LOGGER.info("No config path specified");
      return;
    }
    
    PowerPurgeProcessor processor = new PowerPurgeProcessor();
    processor.setReportOnly(reportOnly);

    for (String configPath : configPaths) {
    
      try {
        LOGGER.info("Processing config: {}", configPath);
        if (reportOnly) {
          LOGGER.info("Running in report-only mode; no changes will be made");
        }    

        List<PathConfig> configs = PathConfig.fromFile(configPath);
        for (PathConfig config : configs) {
          processor.process(config);
        }
      }
      catch (Exception ex) {
        LOGGER.error("Error", ex);
      }
    }
    
    LOGGER.info("Complete. Processed {} paths, archived {} files, deleted {} files, {}",  
      processor.getTotalPathsProcessed(),
      processor.getArchiveTotalCount(),
      processor.getDeleteTotalCount(),
      FileUtils.byteCountToDisplaySize(processor.getGrandTotalSize()));
    
  }

}
