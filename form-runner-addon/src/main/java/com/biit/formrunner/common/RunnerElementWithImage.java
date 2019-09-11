package com.biit.formrunner.common;

import java.util.List;

import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;

public class RunnerElementWithImage extends CustomComponent implements IRunnerElement {
	private static final long serialVersionUID = -8919708962286687743L;
	private static final String CLASSNAME = "vFormRunnerImage";
	private static final String FULL = "100%";
	private IRunnerElement elementComponent;
	private RunnerImage imageComponent;
	private final Runner runner;

	public RunnerElementWithImage(IRunnerElement elementComponent, RunnerImage imageComponent, Runner runner) {
		super();
		this.runner = runner;
		setSizeUndefined();
		HorizontalLayout rootLayout = new HorizontalLayout();
		rootLayout.addStyleName(CLASSNAME);
		rootLayout.addComponent(elementComponent);
		setCompositionRoot(rootLayout);
		rootLayout.setWidth(FULL);
		this.elementComponent = elementComponent;
		this.imageComponent = imageComponent;
		this.setWidth(FULL);
		elementComponent.setWidth(FULL);

		rootLayout.addComponent(elementComponent);
		rootLayout.setComponentAlignment(elementComponent, Alignment.TOP_LEFT);
		rootLayout.addComponent(imageComponent);
		rootLayout.setComponentAlignment(imageComponent, Alignment.TOP_RIGHT);
	}

	public Component getComponent() {
		return elementComponent;
	}

	public Runner getRunner() {
		return runner;
	}

	@Override
	public String getName() {
		return elementComponent.getName();
	}

	@Override
	public List<String> getPath() {
		return elementComponent.getPath();
	}

	@Override
	public IRunnerElement getElement(List<String> subList) throws PathDoesNotExist {
		return elementComponent.getElement(subList);
	}

	@Override
	public void setRelevance(boolean value) {
		elementComponent.setRelevance(value);
	}

	@Override
	public boolean getRelevance() {
		return elementComponent.getRelevance();
	}

	@Override
	public boolean isValid() {
		return elementComponent.isValid();
	}

	@Override
	public void clear() {
		elementComponent.clear();
	}

	@Override
	public List<Result> getAnswers() {
		return elementComponent.getAnswers();
	}

	@Override
	public void setAnswers(List<String> list) throws UnsupportedOperationException {
		elementComponent.setAnswers(list);
	}

	@Override
	public void addElement(IRunnerElement element) {
		elementComponent.addElement(element);
	}

	@Override
	public boolean isMandatory() {
		return elementComponent.isMandatory();
	}

	@Override
	public boolean isFilled() {
		return elementComponent.isFilled();
	}

	@Override
	public void addValueChangedListeners(FieldValueChanged listener) {
		elementComponent.addValueChangedListeners(listener);
	}

	public RunnerImage getImageComponent() {
		return imageComponent;
	}

	public void setImageComponent(RunnerImage imageComponent) {
		this.imageComponent = imageComponent;
	}

	@Override
	public void setTabIndex(int tabIndex) {
		elementComponent.setTabIndex(tabIndex);
	}

	@Override
	public boolean isHidden() {
		return elementComponent.isHidden();
	}

	@Override
	public void setHidden(boolean hidden) {
		elementComponent.setHidden(hidden);
	}

}
