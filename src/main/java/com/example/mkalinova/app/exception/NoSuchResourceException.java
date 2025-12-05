package com.example.mkalinova.app.exception;



public class NoSuchResourceException extends RuntimeException {
	public NoSuchResourceException(String message) {
		super(message);
	}
}