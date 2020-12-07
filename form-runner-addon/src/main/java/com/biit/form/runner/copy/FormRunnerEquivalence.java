package com.biit.form.runner.copy;

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

import com.biit.form.entity.IQuestionWithAnswers;
import com.biit.form.runner.logger.FormRunnerLogger;

/**
 * Defines an equivalence between two questions of two different forms. Also
 * defines the operation that must be performed with this values (copy...)
 */
public class FormRunnerEquivalence {
    private final static String DEFAULT_DATE_FORMAT = "yyy-MM-dd";
    private final static String PATH_DELIMITATOR = "/";
    private String sourcePath;
    private String destinationPath;
    private Operator operator;
    private int priority = 0;
    private Map<String, String> translations = new HashMap<>();
    private IQuestionWithAnswers sourceQuestion;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    /**
     * Return path as a list.
     *
     * @return a list of folders.
     */
    public List<String> getDestinationPathAsList() {
        if (destinationPath == null) {
            return null;
        }
        String[] newPath = destinationPath.split(PATH_DELIMITATOR);
        return Arrays.asList(newPath);
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }

    public String getFormRunnerValue(String originalValue) {
        switch (getOperator()) {
            case GET:
            case PROFILE:
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

    protected String getDateFormat() {
        return DEFAULT_DATE_FORMAT;
    }

    private String getYears(String dateInString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(getDateFormat());
            Date orbeonDate = formatter.parse(dateInString);
            Date now = new Date();
            return getDiffYears(orbeonDate, now) + "";
        } catch (ParseException e) {
            FormRunnerLogger.errorMessage(IntakeFormRunnerEquivalence.class.getName(), e);
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

    @Override
    public String toString() {
        return "Source path '" + getSourcePath() + "'. Destination path '" + getDestinationPath() + "' (Action: " + getOperator() + ")" + " [Priority: "
                + getPriority() + "]: ";
    }

    public IQuestionWithAnswers getSourceQuestion() {
        return sourceQuestion;
    }

    public void setSourceQuestion(IQuestionWithAnswers orbeonQuestion) {
        this.sourceQuestion = orbeonQuestion;
    }

    public Set<String> getFormRunnerAnswers() {
        Set<String> values = new HashSet<>();
        if (getSourceQuestion() != null && getSourceQuestion().getAnswers() != null) {
            for (String value : getSourceQuestion().getAnswers()) {
                values.add(getFormRunnerValue(value));
            }
        }
        return values;
    }

}
