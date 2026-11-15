package com.devanshu.lovableclone.exceptions;

public class DeploymentException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DeploymentException(final String message) {
		super(message);
	}

	public DeploymentException(final String message, final Throwable cause) {
		super(message, cause);
	}
}