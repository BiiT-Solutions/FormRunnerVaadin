package com.biit.form.runner.copy;

import java.util.*;

import com.biit.form.entity.IFormWithAnswers;
import com.biit.form.entity.IQuestionWithAnswers;

/**
 * Defines a relationship between a submitted form and a form runner structure.
 */
public class FormRunnerMatcher {
    private Set<FormRunnerEquivalence> equivalences;

    public FormRunnerMatcher(Set<FormRunnerEquivalence> equivalences) {
        setEquivalences(equivalences);
    }

    /**
     * Returns the form equivalence with answer and highest priority. Also
     * updates the form values in the equivalences.
     *
     * @param question the webform question.
     * @param operator a specific operator to search
     * @return the form element.
     */
    public Set<FormRunnerEquivalence> getFormRunnerEquivalences(IQuestionWithAnswers question, Operator... operator) {
        if (question == null) {
            return new HashSet<>();
        }
        return getFormRunnerEquivalences(question.getPathName(), operator);
    }


    /**
     * Returns the form equivalence with answer and highest priority. Also
     * updates the form values in the equivalences.
     *
     * @param question the webforms question.
     * @return the form element.
     */
    public Set<FormRunnerEquivalence> getFormRunnerEquivalences(IQuestionWithAnswers question) {
        return getFormRunnerEquivalences(question, null);
    }

    public Set<FormRunnerEquivalence> getFormRunnerEquivalences(String formDestinationPath, Operator... operator) {
        Set<FormRunnerEquivalence> equivalencesFound = new HashSet<>();
        if (equivalences != null) {
            for (FormRunnerEquivalence equivalence : equivalences) {
                if (equivalence != null && Objects.equals(equivalence.getDestinationPath(), formDestinationPath)) {
                    // Has value, use it. If not, skip to a second equivalence
                    // definition.
                    try {
                        if (equivalence.getSourceQuestion() != null && equivalence.getSourceQuestion().getAnswers() != null
                                && !equivalence.getSourceQuestion().getAnswers().isEmpty()) {
                            if (operator == null || Arrays.asList(operator).contains(equivalence.getOperator())) {
                                equivalencesFound.add(equivalence);
                            }
                        }
                    } catch (NullPointerException npe) {
                        // No answer selected.
                    }
                }
            }
        }
        return equivalencesFound;
    }

    public Set<FormRunnerEquivalence> getFormRunnerEquivalences(String formSourcePath) {
        Set<FormRunnerEquivalence> equivalencesFound = new HashSet<>();
        if (getEquivalences() != null) {
            for (FormRunnerEquivalence equivalence : getEquivalences()) {
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
     * @param form the answer
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

    protected Set<FormRunnerEquivalence> getEquivalences() {
        return equivalences;
    }
}
