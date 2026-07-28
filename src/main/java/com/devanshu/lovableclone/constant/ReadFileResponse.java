package com.devanshu.lovableclone.constant;

public record ReadFileResponse(String path, Status status, String content, String message) {

	public enum Status {
		FILE_FOUND, FILE_NOT_FOUND, INVALID_REQUEST
	}
}