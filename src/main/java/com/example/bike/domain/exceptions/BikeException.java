package com.example.bike.domain.exceptions;

public class BikeException extends RuntimeException{
    private final int httpStatusCode;
    public BikeException(int httpStatusCode) {
        super();
        this.httpStatusCode = httpStatusCode;
    }

    public BikeException(int httpStatusCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }


    public BikeException(String message, Throwable cause, int httpStatusCode) {
        super(message, cause);
        this.httpStatusCode = httpStatusCode;
    }
}
