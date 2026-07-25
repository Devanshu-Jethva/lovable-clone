package com.devanshu.lovableclone.dto.projects.file;

public record ProjectFileListDTO(String path) {

	@Override
	public String toString() {
		return path;
	}

}
