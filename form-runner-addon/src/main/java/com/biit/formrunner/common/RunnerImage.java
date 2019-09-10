package com.biit.formrunner.common;

import java.util.ArrayList;
import java.util.List;

import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;

public class RunnerImage extends CustomComponent implements IRunnerElement {
	private static final long serialVersionUID = -7148743083612463328L;
	private static final String CLASSNAME = "vFormRunnerImage";
	private Component component;
	private final Runner runner;
	private final String name;
	private final List<String> path;

	public RunnerImage(String name, Component component, Runner runner, List<String> path) {
		super();
		this.name = name;
		this.runner = runner;
		this.path = path;
		setSizeUndefined();
		CssLayout rootLayout = new CssLayout();
		rootLayout.addStyleName(CLASSNAME);
		rootLayout.setWidth("100%");
		this.setWidth("100%");
		rootLayout.addComponent(component);
		setCompositionRoot(rootLayout);
		this.component = component;
	}

	public Component getComponent() {
		return component;
	}

	public Runner getRunner() {
		return runner;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<String> getPath() {
		return path;
	}

	@Override
	public IRunnerElement getElement(List<String> subList) throws PathDoesNotExist {
		throw new PathDoesNotExist(path);
	}

	@Override
	public void setRelevance(boolean value) {
		setVisible(value);
	}

	@Override
	public boolean getRelevance() {
		return isVisible();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void clear() {
		// Do Nothing
	}

	@Override
	public List<Result> getAnswers() {
		return new ArrayList<Result>();
	}

	@Override
	public void setAnswers(List<String> list) throws UnsupportedOperationException {
		// Do Nothing
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
		// Images do not have tab selection
	}

}
