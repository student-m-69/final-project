package com.fileconv.converter.xml;

import com.fileconv.exception.ParseException;
import com.fileconv.model.DataNode;
import com.fileconv.model.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class XmlParserTest {

    private XmlParser parser;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        parser = new XmlParser();
    }

    @Test
    void parseSimpleElements() throws IOException {
        Path file = tempDir.resolve("simple.xml");
        Files.writeString(file, """
                <person>
                    <name>Alice</name>
                    <age>30</age>
                </person>
                """);

        DataNode result = parser.parse(file);
        assertTrue(result.isObject());
        ObjectNode root = result.asObject().get("root").asObject();
        assertEquals("Alice", root.get("name").asValue().asString());
        assertEquals("30", root.get("age").asValue().asString());
    }

    @Test
    void parseNestedElements() throws IOException {
        Path file = tempDir.resolve("nested.xml");
        Files.writeString(file, """
                <person>
                    <name>Bob</name>
                    <address>
                        <city>Vienna</city>
                        <zip>1010</zip>
                    </address>
                </person>
                """);

        DataNode result = parser.parse(file);
        ObjectNode root = result.asObject().get("root").asObject();
        ObjectNode address = root.get("address").asObject();
        assertEquals("Vienna", address.get("city").asValue().asString());
    }

    @Test
    void parseRepeatedElements() throws IOException {
        Path file = tempDir.resolve("repeated.xml");
        Files.writeString(file, """
                <people>
                    <person>Alice</person>
                    <person>Bob</person>
                </people>
                """);

        DataNode result = parser.parse(file);
        ObjectNode root = result.asObject().get("root").asObject();
        assertTrue(root.get("person").isArray());
        assertEquals(2, root.get("person").asArray().size());
    }

    @Test
    void throwsOnInvalidXml() throws IOException {
        Path file = tempDir.resolve("invalid.xml");
        Files.writeString(file, "<unclosed><tag>");

        assertThrows(ParseException.class, () -> parser.parse(file));
    }

    @Test
    void throwsOnNonexistentFile() {
        Path file = tempDir.resolve("missing.xml");
        assertThrows(ParseException.class, () -> parser.parse(file));
    }
}
