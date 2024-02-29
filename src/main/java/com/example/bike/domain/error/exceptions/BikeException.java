package com.example.bike.domain.error.exceptions;

import com.example.bike.domain.enums.ErrorDefinitionEnum;
import com.example.bike.domain.error.exceptions.BaseException;

import java.util.Map;

public class BikeException extends BaseException {
    public BikeException(ErrorDefinitionEnum error, Map<String, String> messageValues, int httpStatus, Throwable cause) {
        super(error, messageValues, httpStatus, cause);
    }

    public BikeException(ErrorDefinitionEnum error, Map<String, String> messageValues, int httpStatus) {
        super(error, messageValues, httpStatus);
    }

    public BikeException(ErrorDefinitionEnum error, String message, int httpStatus) {
        super(error, message, httpStatus);
    }
}
