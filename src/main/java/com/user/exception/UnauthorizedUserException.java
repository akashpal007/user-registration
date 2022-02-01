package com.user.exception;

public class UnauthorizedUserException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1660232925869115749L;

	public UnauthorizedUserException(String message) {
		super(message);
	}

}
