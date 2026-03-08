package com.fileconv.cli;

import java.nio.file.Path;

public record CliArgs(Path inputFile, Path outputFile) {
}
