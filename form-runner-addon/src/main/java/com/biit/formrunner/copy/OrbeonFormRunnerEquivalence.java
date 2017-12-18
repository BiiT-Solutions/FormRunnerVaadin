package com.biit.formrunner.copy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.biit.form.runner.logger.FormRunnerLogger;
import com.biit.form.submitted.ISubmittedQuestion;

public class OrbeonFormRunnerEquivalence {
	// Orbeon format: 1985-12-12
	private final static String ORBEON_DATE_FORMAT = "yyy-MM-dd";
	private final static String PATH_DELIMITATOR = "/";
	private String orbeonPath;
	private String formRunnerPath;
	private Operator operator;
	private int priority = 0;
	private ISubmittedQuestion orbeonQuestion;

	private Map<String, String> translations = new HashMap<>();

	public OrbeonFormRunnerEquivalence(String orbeonPath, String formRunnerPath, String operator) {
		setOrbeonPath(orbeonPath);
		setFormRunnerPath(formRunnerPath);
		setOperator(operator);
	}

	public String getOrbeonPath() {
		return orbeonPath;
	}

	public void setOrbeonPath(String orbeonPath) {
		this.orbeonPath = orbeonPath;
	}

	public String getFormRunnerPath() {
		return formRunnerPath;
	}

	/**
	 * Return path as a list.
	 * 
	 * @return a list of folders.
	 */
	public List<String> getPathAsList() {
		if (formRunnerPath == null) {
			return null;
		}
		String[] newPath = formRunnerPath.split(PATH_DELIMITATOR);
		return Arrays.asList(newPath);
	}

	public void setFormRunnerPath(String formRunnerPath) {
		this.formRunnerPath = formRunnerPath;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setOperator(String operator) {
		this.operator = Operator.get(operator);
	}

	public Set<String> getFormRunnerAnswers() {
		Set<String> values = new HashSet<>();
		if (getOrbeonQuestion() != null && getOrbeonQuestion().getAnswers() != null) {
			for (String value : getOrbeonQuestion().getAnswers()) {
				values.add(getFormRunnerValue(value));
			}
		}
		return values;
	}

	public String getFormRunnerValue(String originalValue) {
		switch (getOperator()) {
		case GET:
		case COPY:
			// Translate if needed
			if (translations.get(originalValue) != null) {
				return translations.get(originalValue);
			} else {
				return originalValue;
			}
		case YEARS_TO_NOW:
			return getYears(originalValue);
		}
		return originalValue;
	}

	private String getYears(String dateInString) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(ORBEON_DATE_FORMAT);
			Date orbeonDate = formatter.parse(dateInString);
			Date now = new Date();
			return getDiffYears(orbeonDate, now) + "";
		} catch (ParseException e) {
			FormRunnerLogger.errorMessage(OrbeonFormRunnerEquivalence.class.getName(), e);
		}
		return "";
	}

	private static int getDiffYears(Date first, Date last) {
		Calendar a = getCalendar(first);
		Calendar b = getCalendar(last);
		int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
		if (a.get(Calendar.MONTH) > b.get(Calendar.MONTH) || (a.get(Calendar.MONTH) == b.get(Calendar.MONTH) && a.get(Calendar.DATE) > b.get(Calendar.DATE))) {
			diff--;
		}
		return diff;
	}

	private static Calendar getCalendar(Date date) {
		Calendar cal = Calendar.getInstance(Locale.US);
		cal.setTime(date);
		return cal;
	}

	public ISubmittedQuestion getOrbeonQuestion() {
		return orbeonQuestion;
	}

	public void setOrbeonQuestion(ISubmittedQuestion orbeonQuestion) {
		this.orbeonQuestion = orbeonQuestion;
	}

	@Override
	public String toString() {
		return "Orbeon path '" + getOrbeonPath() + "',  Form Runner path '" + getFormRunnerPath() + "' (Action: " + getOperator() + ")" + " [Priority: "
				+ getPriority() + "]: " + getFormRunnerAnswers();
	}

	public Map<String, String> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, String> translations) {
		this.translations = translations;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

}
