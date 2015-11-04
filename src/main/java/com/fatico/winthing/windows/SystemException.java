package com.fatico.winthing.windows;

public class SystemException extends RuntimeException {

    public SystemException(final String message) {
        super(message);
    }

    public SystemException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
