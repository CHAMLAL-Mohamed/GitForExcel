package com.twiza.exceptions;

public class SheetWithInconsistentDataException extends RuntimeException {
    private final static String ERROR_MESSAGE_1 = "The key ";
    private final static String ERROR_MESSAGE_2 = " is already inserted with a different Row";

    public SheetWithInconsistentDataException(String message) {
        super(ERROR_MESSAGE_1+message+ERROR_MESSAGE_2);
    }
}
