package com.fileconv.converter;

import com.fileconv.exception.ParseException;
import com.fileconv.model.DataNode;

import java.nio.file.Path;

public interface DataParser {

    DataNode parse(Path inputFile) throws ParseException;
}
