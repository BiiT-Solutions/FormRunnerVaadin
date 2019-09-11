package com.biit.form.runner.copy;

import java.util.HashSet;
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
	public Set<FormRunnerEquivalence> getFormRunnerEquivalences(IQuestionWithAnswers question) {
		if (question == null) {
			return new HashSet<FormRunnerEquivalence>();
		}
		return getFormRunnerEquivalences(question.getPathName());
	}

	public Set<FormRunnerEquivalence> getFormRunnerEquivalences(String formSourcePath) {
		Set<FormRunnerEquivalence> equivalencesFound = new HashSet<>();
		if (equivalences != null) {
			for (FormRunnerEquivalence equivalence : equivalences) {
				if (equivalence.getSourcePath().equals(formSourcePath)) {
					// Has value, use it. If not, skip to a second equivalence
					// definition.
					try {
						if (equivalence.getSourceQuestion() != null && equivalence.getSourceQuestion().getAnswers() != null
								&& !equivalence.getSourceQuestion().getAnswers().iterator().next().isEmpty()) {
							equivalencesFound.add(equivalence);
						}
					} catch (NullPointerException npe) {
						// No answer selected.
					}
				}
			}
		}
		return equivalencesFound;
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
