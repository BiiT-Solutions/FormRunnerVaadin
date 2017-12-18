package com.biit.formrunner.orbeon;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.formrunner.copy.FormRunnerEquivalence;
import com.biit.formrunner.copy.FormRunnerEquivalenceConfigReader;
import com.biit.formrunner.copy.FormRunnerMatcher;

@Test(groups = "orbeonFormRunnerMatcher")
public class OrbeonFormRunnerMatcherTest {
	private final static String ANAMNESE_COPY_CONFIGURATION_FILE = "anamneseCopy.xml";

	@Test
	public void readConfig() {
		FormRunnerMatcher orbeonFormRunnerMatcher = FormRunnerEquivalenceConfigReader.readConfig(ANAMNESE_COPY_CONFIGURATION_FILE);
		Assert.assertNotNull(orbeonFormRunnerMatcher);

		// Create mock question
		MockSubmittedQuestion orbeonQuestion = new MockSubmittedQuestion();
		orbeonQuestion.setPathName("PersonalDetails/profile_gender");

		FormRunnerEquivalence equivalence = orbeonFormRunnerMatcher.getFormRunnerEquivalence(orbeonQuestion);
		// No Orbeon form. Must be null due to no values detected.
		Assert.assertNull(equivalence);
	}
}
