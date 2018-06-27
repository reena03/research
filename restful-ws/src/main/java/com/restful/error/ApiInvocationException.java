package com.restful.error;

public class ApiInvocationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApiInvocationException(String message) {
		super(message);
	}

}