package com.cqsr.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final HttpStatus errorCode;

    public ProductNotFoundException(String message, HttpStatus errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public HttpStatus getErrorCode() {
        return errorCode;
    }
}

