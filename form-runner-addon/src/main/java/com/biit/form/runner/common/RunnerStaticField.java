package com.biit.form.runner.common;

import java.util.ArrayList;
import java.util.List;

import com.biit.form.runner.common.exceptions.PathDoesNotExist;
import com.vaadin.ui.Component;

public class RunnerStaticField extends RunnerElement<Component> {
	private static final long serialVersionUID = -641673682699068156L;
	private static final String DEFAULT_WIDTH = "400px";

	public RunnerStaticField(String name, Component component, Runner runner, List<String> path) {
		super(name, true, component, runner, path);
		component.setWidth(DEFAULT_WIDTH);
	}

	@Override
	public List<Result> getAnswers() {
		return new ArrayList<Result>();
	}

	@Override
	public void clear() {
		// DO nothing
	}

	@Override
	public void setAnswers(List<String> list) {
		// Do nothing
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public IRunnerElement getElement(List<String> path) throws PathDoesNotExist {
		throw new PathDoesNotExist(path);
	}

	@Override
	public void setRelevance(boolean value) {
		relevance = value;
		setVisible(value);
	}

	@Override
	public void addElement(IRunnerElement element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isMandatory() {
		return false;
	}

	@Override
	public boolean isFilled() {
		return true;
	}

	@Override
	public void addValueChangedListeners(FieldValueChanged listener) {
		// Is static field! Never changes.
	}

	@Override
	public void setTabIndex(int tabIndex) {
		// static elements do not select with tab
	}
}
