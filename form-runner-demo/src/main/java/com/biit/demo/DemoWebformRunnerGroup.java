package com.biit.demo;

import com.biit.form.entity.BaseGroup;
import com.biit.form.entity.TreeObject;
import com.biit.formrunner.common.IRunnerElement;
import com.biit.formrunner.common.Runner;
import com.biit.formrunner.webforms.IWebformsRunnerGroup;
import com.biit.formrunner.webforms.WebformsRunnerElementFactory;
import com.biit.formrunner.webforms.WebformsRunnerGroup;

public class DemoWebformRunnerGroup extends WebformsRunnerGroup implements IWebformsRunnerGroup {
	private static final long serialVersionUID = -3485645350589558706L;

	public DemoWebformRunnerGroup(BaseGroup group, Runner runner) {
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
