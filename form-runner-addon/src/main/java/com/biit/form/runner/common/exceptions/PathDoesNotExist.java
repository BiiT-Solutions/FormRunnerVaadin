package com.biit.form.runner.common.exceptions;

import java.util.List;

public class PathDoesNotExist extends Exception {
	private static final long serialVersionUID = -5467974589641818579L;

	public PathDoesNotExist(String path) {
		super("Path '" + path + "' doesn't exist in current form.");
	}

	public PathDoesNotExist(List<String> path) {
		super("Path '" + path + "' doesn't exist in current form.");
	}
}
