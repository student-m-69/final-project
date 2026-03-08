package com.fileconv.converter.csv;

import com.opencsv.CSVWriter;
import com.fileconv.converter.DataWriter;
import com.fileconv.exception.WriteException;
import com.fileconv.model.ArrayNode;
import com.fileconv.model.DataNode;
import com.fileconv.model.ObjectNode;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CsvWriter implements DataWriter {

    @Override
    public void write(DataNode data, Path outputFile) throws WriteException {
        ArrayNode rows = normalizeToArray(data);

        List<Map<String, String>> flatRows = new ArrayList<>();
        Set<String> allKeys = new LinkedHashSet<>();

        for (DataNode row : rows) {
            if (row instanceof ObjectNode obj) {
                Map<String, String> flat = FlatteningUtils.flatten(obj);
                flatRows.add(flat);
                allKeys.addAll(flat.keySet());
            }
        }

        List<String> headers = new ArrayList<>(allKeys);

        try (Writer writer = Files.newBufferedWriter(outputFile);
             CSVWriter csvWriter = new CSVWriter(writer)) {

            csvWriter.writeNext(headers.toArray(new String[0]));

            for (Map<String, String> flatRow : flatRows) {
                String[] line = new String[headers.size()];
                for (int i = 0; i < headers.size(); i++) {
                    line[i] = flatRow.getOrDefault(headers.get(i), "");
                }
                csvWriter.writeNext(line);
            }
        } catch (IOException e) {
            throw new WriteException("Cannot write to '" + outputFile + "': " + e.getMessage(), e);
        }
    }

    private ArrayNode normalizeToArray(DataNode data) {
        if (data instanceof ArrayNode arr) {
            return arr;
        }
        if (data instanceof ObjectNode obj) {
            ArrayNode arr = new ArrayNode();
            arr.add(obj);
            return arr;
        }
        ArrayNode arr = new ArrayNode();
        arr.add(data);
        return arr;
    }
}
