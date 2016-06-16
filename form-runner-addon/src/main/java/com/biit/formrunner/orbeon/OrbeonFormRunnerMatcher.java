package com.biit.formrunner.orbeon;

import java.util.List;
import java.util.Set;

import com.biit.form.submitted.ISubmittedForm;
import com.biit.form.submitted.ISubmittedObject;
import com.biit.form.submitted.ISubmittedQuestion;

/**
 * Defines a relationship between orbeon submitted form and form runner
 * structure.
 */
public class OrbeonFormRunnerMatcher {
	private Set<OrbeonFormRunnerEquivalence> equivalences;

	/**
	 * Returns the Orbeon equivalence with answer and highest priority. Also
	 * updates the Orbeon values in the equivalences.
	 * 
	 * @param orbeonQuestion
	 * @return
	 */
	public OrbeonFormRunnerEquivalence getFormRunnerEquivalence(ISubmittedQuestion orbeonQuestion) {
		for (OrbeonFormRunnerEquivalence equivalence : equivalences) {
			if (equivalence.getOrbeonPath().equals(orbeonQuestion.getPathName())) {
				// Has value, use it. If not, skip to a second equivalence
				// definition.
				if (equivalence.getOrbeonQuestion().getAnswers() != null && !equivalence.getOrbeonQuestion().getAnswers().iterator().next().isEmpty()) {
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

	/**
	 * Set the Orbeon answer to the equivalence.
	 * 
	 * @param orbeonForm
	 */
	public void updateOrbeonAnswers(ISubmittedForm orbeonForm) {
		if (isEnabled()) {
			List<ISubmittedObject> questions = orbeonForm.getChildren(ISubmittedQuestion.class);
			for (ISubmittedObject element : questions) {
				ISubmittedQuestion orbeonQuestion = (ISubmittedQuestion) element;
				for (OrbeonFormRunnerEquivalence equivalence : equivalences) {
					if (equivalence.getOrbeonPath().equals(orbeonQuestion.getPathName())) {
						equivalence.setOrbeonQuestion(orbeonQuestion);
					}
				}
			}
		}
	}

	public String getFormRunnerAnswerEquivalence(String orbeonAnswer) {
		for (OrbeonFormRunnerEquivalence equivalence : equivalences) {
			if (equivalence.getOrbeonPath().equals(orbeonAnswer)) {
				return equivalence.getFormRunnerPath();
			}
		}
		return null;
	}

	protected void setEquivalences(Set<OrbeonFormRunnerEquivalence> equivalences) {
		this.equivalences = equivalences;
	}

	public boolean isEnabled() {
		return equivalences != null && !equivalences.isEmpty();
	}

}
