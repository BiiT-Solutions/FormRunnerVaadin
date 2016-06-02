package com.biit.formrunner.common;

import java.util.List;

import com.biit.form.result.QuestionWithValueResult;
import com.vaadin.ui.TextArea;

public class RunnerTextArea extends RunnerField<TextArea> {
	private static final long serialVersionUID = 5508500902295259209L;
	private static final String DEFAULT_HEIGHT = "80px";

	public RunnerTextArea(String name, String caption, String description, String requiredCaption, boolean isMandatory,
			String defaultValue, Runner runner, List<String> path) {
		super(name, new TextArea(caption), description, isMandatory, requiredCaption, runner, path);
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
