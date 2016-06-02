package com.biit.formrunner.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.biit.form.runner.logger.FormRunnerLogger;
import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractSelect;

public class RunnerSelection<T extends AbstractSelect> extends RunnerElement<T> {
	private static final long serialVersionUID = 2621769528581225023L;
	private final Set<FieldValueChanged> valueChangedListeners;

	public RunnerSelection(String name, T component, String description, boolean isMandatory, String requiredCaption, Runner runner,
			List<String> path) {
		super(name, component, runner, path);
		valueChangedListeners = new HashSet<>();
		component.setDescription(description);
		component.setRequired(isMandatory);
		component.setRequiredError(requiredCaption);
		component.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -8108931765995013712L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if (!getRunner().isLoading()) {
						// An element has changed, notify it.
						launchListeners();
						// Hide or show elements depending on the form flow.
						getRunner().evaluate(getPath());
					}
				} catch (PathDoesNotExist e) {
					// This should not be possible
					FormRunnerLogger.errorMessage(this.getClass().getName(), e);
				}
			}
		});
	}

	@Override
	public List<Result> getAnswers() {
		List<Result> answers = new ArrayList<Result>();
		if (getComponent().getValue() == null) {
			// Empty answer do no fill
		} else {
			if (getComponent().getValue() instanceof Set) {
				@SuppressWarnings("unchecked")
				Set<Object> values = ((Set<Object>) getComponent().getValue());

				// Generate a new question answer with all the answers
				ResultQuestion questionAnswer = new ResultQuestion(getName());
				for (Object value : values) {
					questionAnswer.addAnswer(value.toString());
				}
				answers.add(questionAnswer);
			} else {
				// Generate a new question answer with the selected answer.
				answers.add(new ResultQuestion(getName(), getComponent().getValue().toString()));
			}
		}
		return answers;
	}

	@Override
	public void clear() {
		getComponent().clear();
	}

	@Override
	public void setAnswers(List<String> answers) throws UnsupportedOperationException {
		if (!getComponent().isMultiSelect()) {
			if (answers == null || answers.isEmpty()) {
				getComponent().setValue(null);
			} else {
				getComponent().setValue(answers.get(0));
			}
		} else {
			Set<Object> value = new HashSet<>();
			for (String answer : answers) {
				value.add(answer);
			}
			getComponent().setValue(value);
		}
	}

	@Override
	public boolean isValid() {
		return !getRelevance() || getComponent().isValid();
	}

	@Override
	public boolean isMandatory() {
		return getComponent().isRequired();
	}

	@Override
	public boolean isFilled() {
		return getComponent().getValue() != null;
	}

	@Override
	public void addValueChangedListeners(FieldValueChanged listener) {
		valueChangedListeners.add(listener);
	}

	private void launchListeners() {
		for (FieldValueChanged listener : valueChangedListeners) {
			listener.valueChanged(this);
		}
	}

	@Override
	public void setTabIndex(int tabIndex) {
		getComponent().setTabIndex(tabIndex);
	}
}
