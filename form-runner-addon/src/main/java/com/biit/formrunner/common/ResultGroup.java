package com.biit.formrunner.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultGroup extends Result {

	private final List<Result> answerElements;

	public ResultGroup(String path) {
		super(path);
		answerElements = new ArrayList<>();
	}

	public List<Result> getAnswerElements() {
		return Collections.unmodifiableList(answerElements);
	}

	public void addAnswers(List<Result> answers) {
		answerElements.addAll(answers);
	}
}
