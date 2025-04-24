package com.cloudify.demologin.controller;

import com.cloudify.demologin.dto.response.BaseResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AdvisorController {

    @ExceptionHandler(value = SecurityException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public BaseResponse<?> handleSecurityException(SecurityException e) {
        return BaseResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public BaseResponse<?> handleEntityNotFoundException(EntityNotFoundException e) {
        return BaseResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(value = EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public BaseResponse<?> handleEntityExistsException(EntityExistsException e) {
        return BaseResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> valueValidateException(MethodArgumentNotValidException e) {
        Map<String, String> errors = e.getFieldErrors()
                .stream()
                .collect(HashMap::new, (m, v) -> {
                    String key = v.getField().replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
                    String value = v.getDefaultMessage();
                    m.merge(key, value, (v1, v2) -> v1 + ", " + v2);
                }, HashMap::putAll);

        return BaseResponse.builder()
                .message("Bad Request")
                .errors(errors)
                .build();
    }
}
