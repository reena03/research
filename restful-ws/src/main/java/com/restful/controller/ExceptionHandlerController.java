package com.restful.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.restful.error.ApiInvocationException;
import com.restful.error.InvalidRequestException;

@ControllerAdvice
@RestController
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<String> handleInvalidRequest(InvalidRequestException e) throws IOException {
		return new ResponseEntity<>(e.getMessage(), org.springframework.http.HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ApiInvocationException.class)
	public ResponseEntity<String> handleException(ApiInvocationException e) throws IOException {
		return new ResponseEntity<>(e.getMessage(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
	}
}