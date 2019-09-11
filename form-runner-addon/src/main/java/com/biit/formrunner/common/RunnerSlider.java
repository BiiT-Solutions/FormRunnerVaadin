package com.biit.formrunner.common;

import java.util.List;

import com.biit.form.runner.logger.FormRunnerLogger;
import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;

public class RunnerSlider extends RunnerField<Slider> {
	private static final long serialVersionUID = 5508500902295259321L;

	public RunnerSlider(String name, String caption, double minValue, double maxValue, String description,
			String requiredCaption, boolean mandatory, boolean hidden, Double defaultValue, Runner runner,
			List<String> path) {
		super(name, new Slider(caption), description, mandatory, hidden, requiredCaption, runner, path);
		getComponent().setMin(minValue);
		getComponent().setMax(maxValue);
		if (defaultValue != null) {
			try {
				getComponent().setValue(defaultValue);
			} catch (ValueOutOfBoundsException e) {
			}
		}
	}

	@Override
	public void clear() {
		getComponent().getMin();
	}

	@Override
	public void setAnswers(List<String> answers) throws UnsupportedOperationException {
		if (answers != null && !answers.isEmpty()) {
			try {
				getComponent().setValue(Double.parseDouble(answers.get(0)));
			} catch (NumberFormatException nfe) {
				FormRunnerLogger.errorMessage(this.getClass().getName(),
						"Invalid value '" + answers.get(0) + "' retrieved for slider '" + getName() + "'.");
			}
		}
	}
}
