package com.fileconv.exception;

public abstract class FileConvException extends RuntimeException {

    protected FileConvException(String message) {
        super(message);
    }

    protected FileConvException(String message, Throwable cause) {
        super(message, cause);
    }
}
