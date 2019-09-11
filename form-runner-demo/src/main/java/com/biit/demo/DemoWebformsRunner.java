package com.biit.demo;

import com.biit.form.runner.webforms.IWebformsRunner;
import com.biit.form.runner.webforms.WebformsRunner;
import com.biit.webforms.persistence.entity.Category;

public class DemoWebformsRunner extends WebformsRunner<DemoWebformRunnerGroup> implements IWebformsRunner {
	private static final long serialVersionUID = -7885955783188126246L;

	@Override
	public DemoWebformRunnerGroup createWebformsRunnerGroup(Category category) {
		return new DemoWebformRunnerGroup(category, this);
	}

}
