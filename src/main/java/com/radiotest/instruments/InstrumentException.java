package com.radiotest.instruments;

public class InstrumentException extends Exception {
    public InstrumentException(String message) {
        super(message);
    }

    public InstrumentException(String message, Throwable cause) {
        super(message, cause);
    }
}

