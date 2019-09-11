package com.biit.demo;

import com.biit.form.entity.BaseGroup;
import com.biit.form.entity.TreeObject;
import com.biit.form.runner.common.IRunnerElement;
import com.biit.form.runner.common.Runner;
import com.biit.form.runner.webforms.IWebformsRunnerGroup;
import com.biit.form.runner.webforms.WebformsRunnerElementFactory;
import com.biit.form.runner.webforms.WebformsRunnerTable;

public class DemoWebformRunnerTable extends WebformsRunnerTable implements IWebformsRunnerGroup {
	private static final long serialVersionUID = -3485645350589558706L;

	public DemoWebformRunnerTable(BaseGroup group, Runner runner) {
		super(group, runner);
	}

	@Override
	public IWebformsRunnerGroup getElement(BaseGroup group, Runner runner) {
		return new DemoWebformRunnerGroup(group, runner);
	}

	@Override
	public IRunnerElement getElement(TreeObject element, Runner runner) {
		return WebformsRunnerElementFactory.generateElementWithImage(element, runner);
	}

}
