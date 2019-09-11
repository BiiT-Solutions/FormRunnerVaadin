package com.biit.form.runner.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ResultQuestion extends Result {

	private final List<String> answers;

	public ResultQuestion(String path, String... answer) {
		super(path);
		this.answers = new ArrayList<String>(Arrays.asList(answer));
	}

	public List<String> getAnswers() {
		return Collections.unmodifiableList(answers);
	}

	public String[] getAnswerArray() {
		return answers.toArray(new String[answers.size()]);
	}

	public void addAnswer(String answer) {
		answers.add(answer);
	}

	@Override
	public String toString() {
		return "{" + getPath() + " " + answers + "}";
	}
}
