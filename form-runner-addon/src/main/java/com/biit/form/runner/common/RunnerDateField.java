package com.biit.form.runner.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.DateField;

/**
 * Runner element for datefields. Stores value as a long in string form.
 *
 */
public class RunnerDateField extends RunnerField<DateField> {
	private static final long serialVersionUID = -1618500349071664793L;

	public RunnerDateField(String name, String caption, String description, boolean isMandatory, boolean hidden,
			String requiredCaption, Runner runner, List<String> path) {
		super(name, new DateField(caption), description, isMandatory, hidden, requiredCaption, runner, path);
	}

	@Override
	public List<Result> getAnswers() throws InvalidValueException {
		List<Result> answers = new ArrayList<Result>();
		if (getComponent().getValue() != null && !getComponent().isEmpty()) {
			answers.add(new ResultQuestion(getName(), "" + getComponent().getValue().getTime()));
		}
		return answers;
	}

	@Override
	public void setAnswers(List<String> answers) throws UnsupportedOperationException {
		if (answers != null && !answers.isEmpty()) {
			getComponent().setValue(new Date(Long.parseLong(answers.get(0))));
		}
	}
}
