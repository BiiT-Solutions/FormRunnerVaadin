package com.biit.form.runner.common;

import java.util.List;

import com.biit.form.result.QuestionWithValueResult;
import com.vaadin.ui.TextArea;

public class RunnerTextArea extends RunnerField<TextArea> {
	private static final long serialVersionUID = 5508500902295259209L;
	private static final String DEFAULT_HEIGHT = "80px";

	public RunnerTextArea(String name, String caption, String description, String requiredCaption, boolean mandatory,
			boolean hidden, String defaultValue, Runner runner, List<String> path) {
		super(name, new TextArea(caption), description, mandatory, hidden, requiredCaption, runner, path);
		getComponent().setMaxLength(QuestionWithValueResult.MAX_LENGTH);
		if (defaultValue != null && !defaultValue.isEmpty()) {
			getComponent().setValue(defaultValue);
		}
		getComponent().setHeight(DEFAULT_HEIGHT);
	}

	@Override
	public void clear() {
		getComponent().setValue("");
	}
}
