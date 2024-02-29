package com.example.bike.domain.model;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String code;
    private final String type;
    private final String message;
    private final Object details;

    public ErrorResponse(String code, String type, String message) {
        this(code, type, message, null);
    }

    public ErrorResponse(String code, String type, String message, Object details) {
        this.code = code;
        this.type = type;
        this.message = message;
        this.details = details;
    }
}
