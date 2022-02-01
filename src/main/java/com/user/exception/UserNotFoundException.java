package com.user.exception;

public class UserNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6628750159723882662L;

	public UserNotFoundException(String message) {
		super(message);
	}

}
