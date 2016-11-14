package com.biit.formrunner.common;

import java.util.List;

import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;

public abstract class RunnerElement<T extends Component> extends CustomComponent implements IRunnerElement {
	private static final long serialVersionUID = -5009613631888959037L;
	private static final String CLASSNAME = "vFormRunnerElement";
	private Component component;
	private final Runner runner;
	private final String name;
	private final List<String> path;
	protected boolean relevance;

	public RunnerElement(String name, Component component, Runner runner, List<String> path) {
		super();
		this.name = name;
		this.runner = runner;
		this.path = path;
		setSizeUndefined();
		CssLayout rootLayout = new CssLayout();
		rootLayout.addStyleName(CLASSNAME);
		rootLayout.addComponent(component);
		setCompositionRoot(rootLayout);
		Responsive.makeResponsive(rootLayout);
		this.component = component;
		relevance = false;
	}

	@SuppressWarnings("unchecked")
	public T getComponent() {
		return (T) component;
	}

	public Runner getRunner() {
		return runner;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setRelevance(boolean value) {
		relevance = value;
		setVisible(value);
	}

	@Override
	public IRunnerElement getElement(List<String> path) throws PathDoesNotExist {
		throw new PathDoesNotExist(path);
	}

	@Override
	public void addElement(IRunnerElement element) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<String> getPath() {
		return path;
	}

	@Override
	public boolean getRelevance() {
		return relevance;
	}
}
