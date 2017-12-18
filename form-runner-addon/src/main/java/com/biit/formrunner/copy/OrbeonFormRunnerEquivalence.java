package com.biit.formrunner.copy;


public class OrbeonFormRunnerEquivalence extends FormRunnerEquivalence {
	// Orbeon format: 1985-12-12
	private final static String ORBEON_DATE_FORMAT = "yyy-MM-dd";

	public OrbeonFormRunnerEquivalence(String orbeonPath, String formRunnerPath, String operator) {
		setSourcePath(orbeonPath);
		setDestinationPath(formRunnerPath);
		setOperator(operator);
	}

	@Override
	protected String getDateFormat() {
		return ORBEON_DATE_FORMAT;
	}

	@Override
	public String toString() {
		return "Orbeon path '" + getSourcePath() + "',  Form Runner path '" + getDestinationPath() + "' (Action: " + getOperator() + ")" + " [Priority: "
				+ getPriority() + "]: " + getFormRunnerAnswers();
	}

}
