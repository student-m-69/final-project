package com.fileconv.cli;

import com.fileconv.exception.InvalidArgumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CliParserTest {

    private CliParser parser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parser = new CliParser();
    }

    @Test
    void parseValidArguments() throws IOException {
        Path input = tempDir.resolve("input.json");
        Files.writeString(input, "{}");
        Path output = tempDir.resolve("output.csv");

        CliArgs result = parser.parse(new String[]{"--input", input.toString(), "--output", output.toString()});

        assertEquals(input, result.inputFile());
        assertEquals(output, result.outputFile());
    }

    @Test
    void parseShortFlags() throws IOException {
        Path input = tempDir.resolve("input.json");
        Files.writeString(input, "{}");
        Path output = tempDir.resolve("output.xml");

        CliArgs result = parser.parse(new String[]{"-i", input.toString(), "-o", output.toString()});

        assertEquals(input, result.inputFile());
        assertEquals(output, result.outputFile());
    }

    @Test
    void throwsOnNoArguments() {
        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> parser.parse(new String[]{}));
        assertTrue(ex.getMessage().contains("No arguments provided"));
    }

    @Test
    void throwsOnMissingInput() throws IOException {
        Path output = tempDir.resolve("output.csv");

        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> parser.parse(new String[]{"--output", output.toString()}));
        assertTrue(ex.getMessage().contains("--input"));
    }

    @Test
    void throwsOnMissingOutput() throws IOException {
        Path input = tempDir.resolve("input.json");
        Files.writeString(input, "{}");

        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> parser.parse(new String[]{"--input", input.toString()}));
        assertTrue(ex.getMessage().contains("--output"));
    }

    @Test
    void throwsOnNonexistentInputFile() {
        Path input = tempDir.resolve("nonexistent.json");
        Path output = tempDir.resolve("output.csv");

        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> parser.parse(new String[]{"--input", input.toString(), "--output", output.toString()}));
        assertTrue(ex.getMessage().contains("does not exist"));
    }

    @Test
    void throwsOnUnknownFlag() {
        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> parser.parse(new String[]{"--unknown", "value"}));
        assertTrue(ex.getMessage().contains("Unknown argument"));
    }

    @Test
    void throwsOnMissingInputValue() {
        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> parser.parse(new String[]{"--input"}));
        assertTrue(ex.getMessage().contains("Missing value"));
    }

    @Test
    void throwsOnNonexistentOutputDirectory() throws IOException {
        Path input = tempDir.resolve("input.json");
        Files.writeString(input, "{}");
        Path output = Path.of("/nonexistent/dir/output.csv");

        InvalidArgumentException ex = assertThrows(InvalidArgumentException.class,
                () -> parser.parse(new String[]{"--input", input.toString(), "--output", output.toString()}));
        assertTrue(ex.getMessage().contains("does not exist"));
    }
}
