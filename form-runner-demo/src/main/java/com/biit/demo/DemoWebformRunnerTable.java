package com.biit.demo;

import com.biit.form.entity.BaseGroup;
import com.biit.form.entity.TreeObject;
import com.biit.formrunner.common.IRunnerElement;
import com.biit.formrunner.common.Runner;
import com.biit.formrunner.webforms.IWebformsRunnerGroup;
import com.biit.formrunner.webforms.WebformsRunnerElementFactory;
import com.biit.formrunner.webforms.WebformsRunnerTable;

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
