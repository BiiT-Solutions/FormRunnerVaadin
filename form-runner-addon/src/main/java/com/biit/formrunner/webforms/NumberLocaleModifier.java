package com.biit.formrunner.webforms;

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
			Number number = NumberFormat.getInstance(programLocale).parse(value);
			return NumberFormat.getInstance(databaseLocale).format(number);
		} catch (ParseException e) {
			FormRunnerLogger.errorMessage(NumberLocaleModifier.class.getName(), e);
			return value;
		}
	}

	@Override
	public String modifyToLoad(String value) {
		try {
			Number number = NumberFormat.getInstance(databaseLocale).parse(value);
			return NumberFormat.getInstance(programLocale).format(number);
		} catch (ParseException e) {
			FormRunnerLogger.errorMessage(NumberLocaleModifier.class.getName(), e);
			return value;
		}
	}

}
