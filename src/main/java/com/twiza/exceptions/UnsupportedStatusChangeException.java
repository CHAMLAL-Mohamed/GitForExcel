package com.twiza.exceptions;

public class UnsupportedStatusChangeException extends UnsupportedOperationException {
    private static final String ERROR_MESSAGE = "Status is Already: ";

    public UnsupportedStatusChangeException(String message) {
        super(ERROR_MESSAGE + message);
    }
}
