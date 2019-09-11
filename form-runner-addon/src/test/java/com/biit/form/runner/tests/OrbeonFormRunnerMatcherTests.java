package com.biit.form.runner.tests;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.form.runner.copy.FormRunnerEquivalence;
import com.biit.form.runner.copy.FormRunnerEquivalenceConfigReader;
import com.biit.form.runner.copy.FormRunnerMatcher;
import com.biit.form.runner.mock.orbeon.MockSubmittedQuestion;

@Test(groups = "orbeonFormRunnerMatcher")
public class OrbeonFormRunnerMatcherTests {
	private final static String ANAMNESE_COPY_CONFIGURATION_FILE = "anamneseCopy.xml";

	@Test
	public void readConfig() {
		FormRunnerMatcher orbeonFormRunnerMatcher = FormRunnerEquivalenceConfigReader.readConfig(ANAMNESE_COPY_CONFIGURATION_FILE);
		Assert.assertNotNull(orbeonFormRunnerMatcher);

		// Create mock question
		MockSubmittedQuestion orbeonQuestion = new MockSubmittedQuestion();
		orbeonQuestion.setPathName("PersonalDetails/profile_gender");

		Set<FormRunnerEquivalence> equivalences = orbeonFormRunnerMatcher.getFormRunnerEquivalences(orbeonQuestion);
		// No Orbeon form. Must be empty due to no values detected.
		Assert.assertTrue(equivalences.isEmpty());
	}
}
