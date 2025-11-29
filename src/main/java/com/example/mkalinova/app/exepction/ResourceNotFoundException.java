package com.example.mkalinova.app.exepction;


import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus()
public class ResourceNotFoundException extends RuntimeException {
	
	
	public ResourceNotFoundException(String message) {
		super(message);
	}
}