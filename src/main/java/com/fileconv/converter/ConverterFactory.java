package com.fileconv.converter;

import com.fileconv.converter.csv.CsvParser;
import com.fileconv.converter.csv.CsvWriter;
import com.fileconv.converter.json.JsonParser;
import com.fileconv.converter.json.JsonWriter;
import com.fileconv.converter.xml.XmlParser;
import com.fileconv.converter.xml.XmlWriter;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class ConverterFactory {

    private static final Map<FormatType, Supplier<DataParser>> PARSERS = new EnumMap<>(FormatType.class);
    private static final Map<FormatType, Supplier<DataWriter>> WRITERS = new EnumMap<>(FormatType.class);

    static {
        PARSERS.put(FormatType.JSON, JsonParser::new);
        PARSERS.put(FormatType.XML, XmlParser::new);
        PARSERS.put(FormatType.CSV, CsvParser::new);

        WRITERS.put(FormatType.JSON, JsonWriter::new);
        WRITERS.put(FormatType.XML, XmlWriter::new);
        WRITERS.put(FormatType.CSV, CsvWriter::new);
    }

    private ConverterFactory() {
    }

    public static DataParser createParser(FormatType format) {
        return PARSERS.get(format).get();
    }

    public static DataWriter createWriter(FormatType format) {
        return WRITERS.get(format).get();
    }
}
