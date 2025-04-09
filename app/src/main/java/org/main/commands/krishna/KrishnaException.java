package org.main.commands.krishna;

public class KrishnaException extends RuntimeException {
    public KrishnaException(String message) {
        super(message);
    }

    public KrishnaException(String message, Throwable cause) {
        super(message, cause);
    }
}
