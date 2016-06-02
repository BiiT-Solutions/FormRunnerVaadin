package com.biit.formrunner.orbeon;

public enum Operator {

	COPY("copy"),

	// Useful to convert birthday to age.
	YEARS_TO_NOW("yearsToNow");

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
}
