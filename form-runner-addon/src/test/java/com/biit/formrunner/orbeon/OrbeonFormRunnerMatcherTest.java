package com.biit.formrunner.orbeon;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.biit.formrunner.copy.OrbeonFormRunnerEquivalence;
import com.biit.formrunner.copy.OrbeonFormRunnerEquivalenceConfigReader;
import com.biit.formrunner.copy.OrbeonFormRunnerMatcher;

@Test(groups = "orbeonFormRunnerMatcher")
public class OrbeonFormRunnerMatcherTest {

	@Test
	public void readConfig() {
		OrbeonFormRunnerMatcher orbeonFormRunnerMatcher = OrbeonFormRunnerEquivalenceConfigReader.readConfig();
		Assert.assertNotNull(orbeonFormRunnerMatcher);

		// Create mock question
		MockSubmittedQuestion orbeonQuestion = new MockSubmittedQuestion();
		orbeonQuestion.setPathName("PersonalDetails/profile_gender");

		OrbeonFormRunnerEquivalence equivalence = orbeonFormRunnerMatcher.getFormRunnerEquivalence(orbeonQuestion);
		//No Orbeon form. Must be null due to no values detected.
		Assert.assertNull(equivalence);
	}
}
