package com.fileconv.converter.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import com.fileconv.converter.DataParser;
import com.fileconv.exception.ParseException;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.DataNode;
import com.fileconv.model.ObjectNode;
import com.fileconv.model.ValueNode;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CsvParser implements DataParser {

    @Override
    public DataNode parse(Path inputFile) throws ParseException {
        try (Reader reader = Files.newBufferedReader(inputFile);
             CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

            List<String[]> allRows = csvReader.readAll();
            if (allRows.isEmpty()) {
                return new ArrayNode();
            }

            String[] headers = allRows.get(0);
            ArrayNode result = new ArrayNode();

            boolean hasNestedHeaders = hasNestedKeys(headers);

            for (int rowIdx = 1; rowIdx < allRows.size(); rowIdx++) {
                String[] row = allRows.get(rowIdx);
                if (hasNestedHeaders) {
                    Map<String, String> flat = new LinkedHashMap<>();
                    for (int col = 0; col < headers.length && col < row.length; col++) {
                        flat.put(headers[col], row[col]);
                    }
                    result.add(FlatteningUtils.unflatten(flat));
                } else {
                    ObjectNode obj = new ObjectNode();
                    for (int col = 0; col < headers.length && col < row.length; col++) {
                        obj.put(headers[col], new ValueNode(row[col]));
                    }
                    result.add(obj);
                }
            }

            return result;
        } catch (IOException | CsvException e) {
            throw new ParseException("Failed to parse CSV file '" + inputFile + "': " + e.getMessage(), e);
        }
    }

    private boolean hasNestedKeys(String[] headers) {
        for (String header : headers) {
            if (header.contains(".") || header.contains("[")) {
                return true;
            }
        }
        return false;
    }
}
