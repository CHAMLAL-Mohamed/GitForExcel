package com.twiza.exceptions;

public class WorkbookWithInvalidFormatException extends RuntimeException {
    public WorkbookWithInvalidFormatException(String message) {
        super(message);
    }

    public WorkbookWithInvalidFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
