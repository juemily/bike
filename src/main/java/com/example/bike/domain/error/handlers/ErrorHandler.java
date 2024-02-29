package com.example.bike.domain.error.handlers;


import com.example.bike.domain.enums.ErrorDefinitionEnum;
import com.example.bike.domain.error.exceptions.BaseException;
import com.example.bike.domain.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<Object> handleBaseException(BaseException ex) {
        log.error("Response error {}, {}, {}", ex.getCode(), ex.getType(), ex.getMessage());
        ErrorResponse er = new ErrorResponse(ex.getCode(), ex.getType(), ex.getMessage());
        return new ResponseEntity<>(er, HttpStatus.valueOf(ex.getHttpStatus()));
    }

    @ExceptionHandler({NullPointerException.class})
    public ResponseEntity<Object> handleNullPointerException(NullPointerException ex) {
        log.error("NullPointer {}", ex.getMessage());
        ErrorResponse er = new ErrorResponse(ErrorDefinitionEnum.GENERIC_ERROR.getErrorCode(),
                                             ErrorDefinitionEnum.GENERIC_ERROR.getType(),
                                             ex.getMessage());
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse er = new ErrorResponse(ErrorDefinitionEnum.GENERIC_JSON_ERROR.getErrorCode(),
                                             ErrorDefinitionEnum.GENERIC_JSON_ERROR.getType(),
                                             ex.getMessage());
        log.error("Response error {}, {}, {}", er.getCode(), er.getType(), er.getMessage());
        return new ResponseEntity<>(er, status);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error("Response error {}", ex.getMessage());
        ErrorResponse er = new ErrorResponse(String.valueOf(ErrorDefinitionEnum.GENERIC_ERROR.getErrorCode()), ErrorDefinitionEnum.GENERIC_ERROR.getType(), ex.getMessage());
        return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("Response error {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            log.error("Error validating object: {}, code: {}, field: {}", error.getObjectName(), error.getCode(), ((FieldError) error).getField());
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse er = new ErrorResponse(String.valueOf(ErrorDefinitionEnum.VALIDATION_ERROR.getErrorCode()),
                                             ErrorDefinitionEnum.VALIDATION_ERROR_AUTO.getType(),
                                             ErrorDefinitionEnum.VALIDATION_ERROR_AUTO.getDefaultMessage(),
                                             errors);
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

}
