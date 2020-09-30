package com.twiza.exceptions;

public class HeaderNotMatchingException extends RuntimeException {
    public HeaderNotMatchingException(String message) {
        super(message);
    }

    public HeaderNotMatchingException(String message, Throwable cause) {
        super(message, cause);
    }
}
