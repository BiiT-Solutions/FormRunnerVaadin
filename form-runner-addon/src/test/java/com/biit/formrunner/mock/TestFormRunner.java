package com.biit.formrunner.mock;

import com.biit.formrunner.webforms.IWebformsRunner;
import com.biit.formrunner.webforms.WebformsRunner;
import com.biit.webforms.persistence.entity.Category;

public class TestFormRunner extends WebformsRunner<TestFormRunnerGroup> implements IWebformsRunner {
	private static final long serialVersionUID = 163595461530920205L;

	@Override
	public TestFormRunnerGroup createWebformsRunnerGroup(Category category) {
		return new TestFormRunnerGroup(category, this);
	}

}