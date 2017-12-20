package com.biit.formrunner.copy;

import java.util.List;
import java.util.Set;

import com.biit.form.entity.IFormWithAnswers;
import com.biit.form.entity.IQuestionWithAnswers;

/**
 * Defines a relationship between a submitted form and a form runner structure.
 */
public class FormRunnerMatcher {
	private Set<FormRunnerEquivalence> equivalences;

	/**
	 * Returns the form equivalence with answer and highest priority. Also
	 * updates the form values in the equivalences.
	 * 
	 * @param question
	 *            the webform question.
	 * @return the form element.
	 */
	public FormRunnerEquivalence getFormRunnerEquivalence(IQuestionWithAnswers question) {
		if (equivalences != null) {
			for (FormRunnerEquivalence equivalence : equivalences) {
				if (equivalence.getSourcePath().equals(question.getPathName())) {
					// Has value, use it. If not, skip to a second equivalence
					// definition.
					if (equivalence.getSourceQuestion().getAnswers() != null && !equivalence.getSourceQuestion().getAnswers().iterator().next().isEmpty()) {
						return equivalence;
					}
				}
			}
		}
		return null;
	}

	public FormRunnerEquivalence getFormRunnerEquivalence(String formSourcePath) {
		for (FormRunnerEquivalence equivalence : equivalences) {
			if (equivalence.getSourcePath().equals(formSourcePath)) {
				return equivalence;
			}
		}
		return null;
	}

	/**
	 * Set the form answer to the equivalence.
	 * 
	 * @param form
	 *            the answer
	 */
	public void updateFormAnswers(IFormWithAnswers form) {
		if (isEnabled() && form != null) {
			List<IQuestionWithAnswers> questions = form.getQuestionsWithAnswers();
			for (IQuestionWithAnswers question : questions) {
				for (FormRunnerEquivalence equivalence : equivalences) {
					if (equivalence.getSourcePath().equals(question.getPathName())) {
						equivalence.setSourceQuestion(question);
					}
				}
			}
		}
	}

	public String getFormRunnerAnswerEquivalence(String answer) {
		for (FormRunnerEquivalence equivalence : equivalences) {
			if (equivalence.getSourcePath().equals(answer)) {
				return equivalence.getDestinationPath();
			}
		}
		return null;
	}

	protected void setEquivalences(Set<FormRunnerEquivalence> equivalences) {
		this.equivalences = equivalences;
	}

	public boolean isEnabled() {
		return equivalences != null && !equivalences.isEmpty();
	}

}
