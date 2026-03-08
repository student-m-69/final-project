package com.fileconv.converter;

import com.fileconv.converter.csv.CsvParser;
import com.fileconv.converter.csv.CsvWriter;
import com.fileconv.converter.json.JsonParser;
import com.fileconv.converter.json.JsonWriter;
import com.fileconv.converter.xml.XmlParser;
import com.fileconv.converter.xml.XmlWriter;
import com.fileconv.exception.UnsupportedFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConverterFactoryTest {

    @Test
    void createsJsonParser() {
        assertInstanceOf(JsonParser.class, ConverterFactory.createParser(FormatType.JSON));
    }

    @Test
    void createsXmlParser() {
        assertInstanceOf(XmlParser.class, ConverterFactory.createParser(FormatType.XML));
    }

    @Test
    void createsCsvParser() {
        assertInstanceOf(CsvParser.class, ConverterFactory.createParser(FormatType.CSV));
    }

    @Test
    void createsJsonWriter() {
        assertInstanceOf(JsonWriter.class, ConverterFactory.createWriter(FormatType.JSON));
    }

    @Test
    void createsXmlWriter() {
        assertInstanceOf(XmlWriter.class, ConverterFactory.createWriter(FormatType.XML));
    }

    @Test
    void createsCsvWriter() {
        assertInstanceOf(CsvWriter.class, ConverterFactory.createWriter(FormatType.CSV));
    }

    @Test
    void formatTypeFromExtension() {
        assertEquals(FormatType.JSON, FormatType.fromExtension("json"));
        assertEquals(FormatType.XML, FormatType.fromExtension("xml"));
        assertEquals(FormatType.CSV, FormatType.fromExtension("csv"));
        assertEquals(FormatType.JSON, FormatType.fromExtension(".json"));
        assertEquals(FormatType.CSV, FormatType.fromExtension("CSV"));
    }

    @Test
    void formatTypeFromUnsupportedExtension() {
        assertThrows(UnsupportedFormatException.class, () -> FormatType.fromExtension("yaml"));
        assertThrows(UnsupportedFormatException.class, () -> FormatType.fromExtension("txt"));
    }
}
