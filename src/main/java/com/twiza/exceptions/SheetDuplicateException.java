package com.twiza.exceptions;

public class SheetDuplicateException extends Exception {

    public SheetDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
    public SheetDuplicateException(String message) {
        super(message);
    }
}
