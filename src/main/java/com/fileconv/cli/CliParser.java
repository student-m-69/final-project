package com.fileconv.cli;

import com.fileconv.exception.InvalidArgumentException;

import java.nio.file.Files;
import java.nio.file.Path;

public class CliParser {

    private static final String USAGE = "Usage: fileconv --input <file> --output <file>";

    public CliArgs parse(String[] args) {
        if (args.length == 0) {
            throw new InvalidArgumentException("No arguments provided. " + USAGE);
        }

        String inputPath = null;
        String outputPath = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--input", "-i" -> {
                    if (i + 1 >= args.length) {
                        throw new InvalidArgumentException("Missing value for --input flag. " + USAGE);
                    }
                    inputPath = args[++i];
                }
                case "--output", "-o" -> {
                    if (i + 1 >= args.length) {
                        throw new InvalidArgumentException("Missing value for --output flag. " + USAGE);
                    }
                    outputPath = args[++i];
                }
                default -> throw new InvalidArgumentException(
                        "Unknown argument: '" + args[i] + "'. " + USAGE);
            }
        }

        if (inputPath == null) {
            throw new InvalidArgumentException("Missing required flag: --input. " + USAGE);
        }
        if (outputPath == null) {
            throw new InvalidArgumentException("Missing required flag: --output. " + USAGE);
        }

        Path input = Path.of(inputPath);
        Path output = Path.of(outputPath);

        if (!Files.exists(input)) {
            throw new InvalidArgumentException("Input file '" + inputPath + "' does not exist.");
        }
        if (!Files.isReadable(input)) {
            throw new InvalidArgumentException("Input file '" + inputPath + "' is not readable.");
        }

        Path outputParent = output.getParent();
        if (outputParent != null && !Files.isDirectory(outputParent)) {
            throw new InvalidArgumentException(
                    "Output directory '" + outputParent + "' does not exist.");
        }

        return new CliArgs(input, output);
    }
}
