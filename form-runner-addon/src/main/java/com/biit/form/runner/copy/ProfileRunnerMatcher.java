package com.biit.form.runner.copy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProfileRunnerMatcher extends FormRunnerMatcher {
    public ProfileRunnerMatcher(Set<FormRunnerEquivalence> equivalences) {
        super(equivalences);
    }

    public Set<FormRunnerEquivalence> getFormRunnerEquivalences(String formDestinationPath, Operator operator) {
        Set<FormRunnerEquivalence> equivalencesFound = new HashSet<>();
        if (getEquivalences() != null) {
            for (FormRunnerEquivalence equivalence : getEquivalences()) {
                if (equivalence.getDestinationPath().equals(formDestinationPath)) {
                    // Has value, use it. If not, skip to a second equivalence
                    // definition.
                    try {
                        if (operator == null || Objects.equals(operator, equivalence.getOperator())) {
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
