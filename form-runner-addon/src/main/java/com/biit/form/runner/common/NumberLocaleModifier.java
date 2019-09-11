package com.biit.form.runner.common;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.biit.form.runner.logger.FormRunnerLogger;

public class NumberLocaleModifier implements ValueModifier {

	private final Locale programLocale;
	private final Locale databaseLocale;

	public NumberLocaleModifier(Locale sourceLocale, Locale destinyLocale) {
		this.programLocale = sourceLocale;
		this.databaseLocale = destinyLocale;
	}

	@Override
	public String modifyToSave(String value) {
		try {
			FormRunnerLogger.debug(this.getClass().getName(), "");
			Number number = NumberFormat.getInstance(programLocale).parse(value);
			NumberFormat formatter = NumberFormat.getInstance(databaseLocale);
			// Remove grouping separator for storing.
			formatter.setGroupingUsed(false);
			String finalValue = formatter.format(number);

			FormRunnerLogger.debug(this.getClass().getName(), "Inserting value '" + value + "' with locale '" + programLocale + "' changed to '" + finalValue
					+ "' with locale '" + databaseLocale + "' into database.");
			return finalValue;
		} catch (ParseException e) {
			FormRunnerLogger.errorMessage(NumberLocaleModifier.class.getName(), e);
			return value;
		}
	}

	@Override
	public String modifyToLoad(String value) {
		try {
			Number number = NumberFormat.getInstance(databaseLocale).parse(value);
			String finalValue = NumberFormat.getInstance(programLocale).format(number);
			FormRunnerLogger.debug(this.getClass().getName(), "Retrieving '" + value + "' with locale '" + programLocale + "' as '" + finalValue
					+ "' with locale '" + databaseLocale + "' to the UI.");
			return finalValue;
		} catch (ParseException e) {
			FormRunnerLogger.errorMessage(NumberLocaleModifier.class.getName(), e);
			return value;
		}
	}

}
