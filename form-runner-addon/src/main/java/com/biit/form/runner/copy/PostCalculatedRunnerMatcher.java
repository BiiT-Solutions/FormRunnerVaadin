package com.biit.form.runner.copy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Equivalences not stores yet data that must be copied.
 */
public class PostCalculatedRunnerMatcher extends FormRunnerMatcher {
    public PostCalculatedRunnerMatcher(Set<FormRunnerEquivalence> equivalences) {
        super(equivalences);
    }

    @Override
    public Set<FormRunnerEquivalence> getFormRunnerEquivalences(String formDestinationPath, Operator... operator) {
        Set<FormRunnerEquivalence> equivalencesFound = new HashSet<>();
        if (getEquivalences() != null) {
            for (FormRunnerEquivalence equivalence : getEquivalences()) {
                if (equivalence.getDestinationPath().equals(formDestinationPath)) {
                    // Has value, use it. If not, skip to a second equivalence
                    // definition.
                    try {
                        //Form equivalence
                        if (equivalence.getOperator().isFormBaseOperator()) {
                            equivalencesFound.add(equivalence);
                        } else
                            //Question equivalence
                            if (operator == null || Arrays.asList(operator).contains(equivalence.getOperator())) {
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
}
