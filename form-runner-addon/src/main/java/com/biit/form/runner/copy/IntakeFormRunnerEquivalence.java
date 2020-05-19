package com.biit.form.runner.copy;


public class IntakeFormRunnerEquivalence extends FormRunnerEquivalence {
	// Orbeon format: 1985-12-12
	private final static String DATE_FORMAT = "yyy-MM-dd";

	public IntakeFormRunnerEquivalence(String orbeonPath, String formRunnerPath, String operator) {
		setSourcePath(orbeonPath);
		setDestinationPath(formRunnerPath);
		setOperator(operator);
	}

	@Override
	protected String getDateFormat() {
		return DATE_FORMAT;
	}

	@Override
	public String toString() {
		return "Path '" + getSourcePath() + "',  Form Runner path '" + getDestinationPath() + "' (Action: " + getOperator() + ")" + " [Priority: "
				+ getPriority() + "]: " + getFormRunnerAnswers();
	}

}
