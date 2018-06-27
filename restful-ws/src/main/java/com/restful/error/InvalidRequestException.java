package com.restful.error;

public class InvalidRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidRequestException(String message) {
		super(message);
	}

	public InvalidRequestException(String message, Exception e) {
		super(message);
	}

}