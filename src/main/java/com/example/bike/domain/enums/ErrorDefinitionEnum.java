package com.example.bike.domain.enums;

public enum ErrorDefinitionEnum {

    GENERIC_ERROR("BIKE-100000", "ERROR", "Error inesperado ${error:-}"),
    GENERIC_JSON_ERROR("BIKE-100001", "ERROR", "Error parseando JSON ${error:-}"),
    GENERIC_CREATE_ERROR("BIKE-100002", "CREATE_ERROR", "Error creando nuevo ${resource:-} ${error:-}"),

    VALIDATION_ERROR("BIKE-100007", "VALIDATION_ERROR", "Error validando datos de entrada, ${error:-}"),
    VALIDATION_ERROR_AUTO("BIKE-100007", "VALIDATION_ERROR", "Error validando datos de entrada"),

    AUTHENTICATION_ERROR("BIKE-100100", "AUTHENTICATION_ERROR", "Error de autenticación. ${error:-}");
    private final String type;

    private final String errorCode;

    private final String defaultMessage;

    ErrorDefinitionEnum(String type, String errorCode, String defaultMessage) {
        this.type = type;
        this.errorCode = errorCode;
        this.defaultMessage = defaultMessage;
    }

    public String getType() {
        return type;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }



}
