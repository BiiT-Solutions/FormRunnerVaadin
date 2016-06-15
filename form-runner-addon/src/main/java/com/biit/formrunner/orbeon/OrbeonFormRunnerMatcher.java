package com.biit.formrunner.orbeon;

import java.util.Set;

import com.biit.form.submitted.ISubmittedQuestion;

public class OrbeonFormRunnerMatcher {
	private Set<OrbeonFormRunnerEquivalence> equivalences;

	/**
	 * Returns the orbeon equivalence with answer and highest priority
	 * 
	 * @param orbeonQuestion
	 * @return
	 */
	public OrbeonFormRunnerEquivalence getFormRunnerEquivalence(ISubmittedQuestion orbeonQuestion) {
		for (OrbeonFormRunnerEquivalence equivalence : equivalences) {
			if (equivalence.getOrbeonPath().equals(orbeonQuestion.getPathName())) {
				// Update Orbeon element
				equivalence.setOrbeonQuestion(orbeonQuestion);
				// Has value, use it. If not, skip to a second equivalence
				// definition.
				if (equivalence.getOrbeonQuestion().getAnswers() != null
						&& !equivalence.getOrbeonQuestion().getAnswers().iterator().next().isEmpty()) {
					return equivalence;
				}
			}
		}
		return null;
	}

	public OrbeonFormRunnerEquivalence getFormRunnerEquivalence(String orbeonPath) {
		for (OrbeonFormRunnerEquivalence equivalence : equivalences) {
			if (equivalence.getOrbeonPath().equals(orbeonPath)) {
				return equivalence;
			}
		}
		return null;
	}

	public String getFormRunnerAnswerEquivalence(String orbeonAnswer) {
		for (OrbeonFormRunnerEquivalence equivalence : equivalences) {
			if (equivalence.getOrbeonPath().equals(orbeonAnswer)) {
				return equivalence.getFormRunnerPath();
			}
		}
		return null;
	}

	public void setEquivalences(Set<OrbeonFormRunnerEquivalence> equivalences) {
		this.equivalences = equivalences;
	}

	public boolean isEnabled() {
		return !equivalences.isEmpty();
	}

}
