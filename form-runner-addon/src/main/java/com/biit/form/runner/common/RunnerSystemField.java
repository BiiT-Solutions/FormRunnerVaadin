package com.biit.form.runner.common;

import java.util.List;

import com.vaadin.ui.Component;

public class RunnerSystemField extends RunnerStaticField {
	private static final long serialVersionUID = 6772149497462592313L;

	public RunnerSystemField(String name, Component component, Runner runner, List<String> path) {
		super(name, component, runner, path);
	}

	@Override
	public void setRelevance(boolean value) {
		relevance = value;
		if (getRunner().isSystemFieldsIgnored()) {
			setVisible(false);
		} else {
			setVisible(value);
		}
	}

}
