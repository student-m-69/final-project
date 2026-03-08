package com.fileconv.converter;

import com.fileconv.exception.UnsupportedFormatException;

public enum FormatType {
    JSON, XML, CSV;

    public static FormatType fromExtension(String extension) {
        String ext = extension.toLowerCase().replaceFirst("^\\.", "");
        return switch (ext) {
            case "json" -> JSON;
            case "xml" -> XML;
            case "csv" -> CSV;
            default -> throw new UnsupportedFormatException(
                    "Unsupported format '." + ext + "'. Supported formats: json, xml, csv.");
        };
    }
}
