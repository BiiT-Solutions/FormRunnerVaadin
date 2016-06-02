package com.biit.formrunner.webforms;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.biit.webforms.utils.math.domain.range.RealRangeDouble;
import com.vaadin.data.Validator;

public class DoubleValidator implements Validator {
	private static final long serialVersionUID = -8637484808522627867L;
	private RealRangeDouble range;
	private Locale locale;
	private String invalidInput;

	public DoubleValidator(Locale locale, String invalidInput) {
		this(locale, invalidInput, RealRangeDouble.fullRange());
	}

	public DoubleValidator(Locale locale, String invalidInput, RealRangeDouble range) {
		this.locale = locale;
		this.range = range;
		this.invalidInput = invalidInput;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		NumberFormat localeNumberFormat = NumberFormat.getInstance(locale);
		try {
			if (!((String) value).isEmpty()) {
				double parsedValue = localeNumberFormat.parse((String) value).doubleValue();
				if (!range.contains(parsedValue)) {
					throw new InvalidValueException(invalidInput);
				}
			}
		} catch (ParseException e) {
			throw new InvalidValueException(invalidInput);
		}
	}

	public static Double getParsedValue(Locale locale, String value) throws ParseException {
		NumberFormat localeNumberFormat = NumberFormat.getInstance(locale);
		if (!((String) value).isEmpty()) {
			return localeNumberFormat.parse((String) value).doubleValue();
		} else {
			return 0.0;
		}
	}

	public static String representValue(Locale locale, Double number) {
		NumberFormat localeNumberFormat = NumberFormat.getInstance(locale);
		if (number != null) {
			return localeNumberFormat.format(number);
		} else {
			return "";
		}
	}

	public static Validator positiveValidation(Locale locale, String invalidInput) {
		return new DoubleValidator(locale, invalidInput, RealRangeDouble.positiveRange());
	}

	public static Validator negativeValidation(Locale locale, String invalidInput) {
		return new DoubleValidator(locale, invalidInput, RealRangeDouble.negativeRange());
	}
}
