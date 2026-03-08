package com.fileconv.converter;

import com.fileconv.exception.WriteException;
import com.fileconv.model.DataNode;

import java.nio.file.Path;

public interface DataWriter {

    void write(DataNode data, Path outputFile) throws WriteException;
}
