package com.biit.formrunner.common;

import java.util.List;

import com.vaadin.ui.Slider;
import com.vaadin.ui.Slider.ValueOutOfBoundsException;

public class RunnerSlider extends RunnerField<Slider> {
	private static final long serialVersionUID = 5508500902295259321L;

	public RunnerSlider(String name, String caption, double minValue, double maxValue, String description,
			String requiredCaption, boolean isMandatory, Double defaultValue, Runner runner, List<String> path) {
		super(name, new Slider(caption), description, isMandatory, requiredCaption, runner, path);
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
}
