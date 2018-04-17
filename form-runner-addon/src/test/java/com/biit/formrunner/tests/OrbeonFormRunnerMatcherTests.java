package com.biit.formrunner.tests;

import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.formrunner.copy.FormRunnerEquivalence;
import com.biit.formrunner.copy.FormRunnerEquivalenceConfigReader;
import com.biit.formrunner.copy.FormRunnerMatcher;
import com.biit.formrunner.mock.orbeon.MockSubmittedQuestion;

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
