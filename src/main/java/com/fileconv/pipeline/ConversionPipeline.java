package com.fileconv.pipeline;

import com.fileconv.cli.CliArgs;
import com.fileconv.converter.ConverterFactory;
import com.fileconv.converter.DataParser;
import com.fileconv.converter.DataWriter;
import com.fileconv.converter.FormatType;
import com.fileconv.exception.FileConvException;
import com.fileconv.model.DataNode;

import java.nio.file.Path;

public class ConversionPipeline {

    public void execute(CliArgs args) throws FileConvException {
        FormatType inputFormat = FormatType.fromExtension(getExtension(args.inputFile()));
        FormatType outputFormat = FormatType.fromExtension(getExtension(args.outputFile()));

        DataParser parser = ConverterFactory.createParser(inputFormat);
        DataWriter writer = ConverterFactory.createWriter(outputFormat);

        DataNode data = parser.parse(args.inputFile());
        writer.write(data, args.outputFile());
    }

    private String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
