package com.example.bike.domain.error.exceptions;

import com.example.bike.domain.enums.ErrorDefinitionEnum;
import org.apache.commons.text.StringSubstitutor;

import java.util.Map;

public class BaseException extends Exception{
    private final int httpStatus;

    private final String code;

    private final String type;

    private final String message;

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BaseException(ErrorDefinitionEnum error, Map<String, String> messageValues, int httpStatus, Throwable cause) {
        super(cause);

        this.code = error.getErrorCode();
        this.type = error.getType();
        this.httpStatus = httpStatus;

        if (messageValues != null) {
            String causeMessage = (cause != null) ? cause.getMessage() : "";
            StringSubstitutor substitutor = new StringSubstitutor(messageValues);
            String finalMessage = substitutor.replace(error.getDefaultMessage());
            if (!causeMessage.isBlank()) {
                finalMessage = String.format(finalMessage + " (%s)", causeMessage);
            }
            this.message = finalMessage;
        } else {
            this.message = (cause != null)? cause.getMessage() : "";
        }
    }

    public BaseException(ErrorDefinitionEnum error, Map<String, String> messageValues, int httpStatus) {
        this(error, messageValues, httpStatus, null);
    }

    public BaseException(ErrorDefinitionEnum error, String message, int httpStatus) {
        this.code = error.getErrorCode();
        this.type = error.getType();
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
