package com.fileconv.exception;

public class WriteException extends FileConvException {

    public WriteException(String message) {
        super(message);
    }

    public WriteException(String message, Throwable cause) {
        super(message, cause);
    }
}
