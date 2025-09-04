package com.cqsr.exception;

import org.springframework.http.HttpStatus;

public class PriceValueException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private HttpStatus httpStatus;
	
	public PriceValueException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}
	
}
