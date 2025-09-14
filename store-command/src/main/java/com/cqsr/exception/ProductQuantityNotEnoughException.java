package com.cqsr.exception;

import org.springframework.http.HttpStatus;

public class ProductQuantityNotEnoughException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    private final HttpStatus httpStatus;

    public ProductQuantityNotEnoughException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
    
}
