package com.biit.formrunner.common;

public abstract class Result {

	private final String path;

	public Result(String path) {
		this.path = path;
	}

	public String getPath() {
		return new String(path);
	}
}
