package com.biit.form.runner.copy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum Operator {

    COPY("copy"),

    COPY_FORM("copyForm"),

    COPY_FROM_PREVIOUS("copyFromPrevious"),

    COPY_PREVIOUS_FORM("copyPreviousForm"),

    GET("get"),

    // Useful to convert birthday to age.
    YEARS_TO_NOW("yearsToNow"),

    PROFILE("profile");

    private String tag;

    Operator(String tag) {
        this.tag = tag;
    }

    public static Operator get(String tag) {
        for (Operator operator : Operator.values()) {
            if (operator.getTag().equals(tag)) {
                return operator;
            }
        }
        return COPY;
    }

    public String getTag() {
        return tag;
    }

    public static List<Operator> getFormBasedOperatos() {
        return Arrays.asList(Operator.COPY_PREVIOUS_FORM, Operator.COPY_FORM);
    }

    public boolean isFormBaseOperator(){
        return getFormBasedOperatos().contains(this);
    }
}
