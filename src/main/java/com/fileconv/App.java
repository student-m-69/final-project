package com.fileconv;

import com.fileconv.cli.CliArgs;
import com.fileconv.cli.CliParser;
import com.fileconv.exception.FileConvException;
import com.fileconv.pipeline.ConversionPipeline;

public class App {

    public static void main(String[] args) {
        try {
            CliParser cliParser = new CliParser();
            CliArgs cliArgs = cliParser.parse(args);

            ConversionPipeline pipeline = new ConversionPipeline();
            pipeline.execute(cliArgs);

            System.out.println("Conversion completed successfully: "
                    + cliArgs.inputFile() + " -> " + cliArgs.outputFile());
        } catch (FileConvException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            System.exit(2);
        }
    }
}
