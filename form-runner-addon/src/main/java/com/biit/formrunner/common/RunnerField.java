package com.biit.formrunner.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.biit.form.runner.logger.FormRunnerLogger;
import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.ui.AbstractField;

public class RunnerField<T extends AbstractField<?>> extends RunnerElement<T> {
	private static final long serialVersionUID = 5280145760638258163L;
	private static final String DEFAULT_WIDTH = "400px";
	private final Set<FieldValueChanged> valueChangedListeners;
	private ValueModifier valueModifier;

	public RunnerField(String name, AbstractField<?> component, String description, boolean isMandatory, String requiredCaption, Runner runner,
			List<String> path) {
		super(name, component, runner, path);
		valueChangedListeners = new HashSet<>();
		component.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = -2447760623181439676L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				try {
					if (!getRunner().isLoading()) {
						// We don't want to log the invalid value exception
						// so we don't use the getComponent().validate() call
						getComponent().isValid();
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
		component.setDescription(description);
		component.setRequired(isMandatory);
		component.setRequiredError(requiredCaption);
		component.setWidth(DEFAULT_WIDTH);
	}

	@Override
	public List<Result> getAnswers() throws InvalidValueException {
		List<Result> answers = new ArrayList<Result>();
		if (getComponent().getValue() != null && !getComponent().getValue().toString().isEmpty() && getComponent().isValid()) {
			if (valueModifier != null) {
				answers.add(new ResultQuestion(getName(), valueModifier.modifyToSave(getComponent().getValue().toString())));
			} else {
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
		// We need to lock the component to avoid
		// java.lang.IllegalStateException: A connector should not be marked as
		// dirty while a response is being written.
		// As described at https://vaadin.com/forum#!/thread/1820683
		if (getComponent() != null && getComponent().getUI() != null && getComponent().getUI().getSession() != null
				&& getComponent().getUI().getSession().getLockInstance() != null) {
			getComponent().getUI().getSession().getLockInstance().lock();
		}
		try {
			if (valueModifier != null) {
				getComponent().setConvertedValue(valueModifier.modifyToLoad(answers.get(0)));
			} else {
				getComponent().setConvertedValue(answers.get(0));
			}
		} finally {
			if (getComponent() != null && getComponent().getUI() != null && getComponent().getUI().getSession() != null
					&& getComponent().getUI().getSession().getLockInstance() != null) {
				getComponent().getUI().getSession().getLockInstance().unlock();
			}
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
		return getComponent().isValid();
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

	public void setValueModifier(ValueModifier valueModifier) {
		this.valueModifier = valueModifier;
	}

	@Override
	public void setLocale(Locale locale) {
		getComponent().setLocale(locale);
	}
}
