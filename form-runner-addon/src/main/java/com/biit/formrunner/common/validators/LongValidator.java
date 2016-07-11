package com.biit.formrunner.common.validators;

import com.biit.webforms.utils.math.domain.range.RealRangeLong;
import com.vaadin.data.Validator;

public class LongValidator implements Validator {
	private static final long serialVersionUID = 2415283297168267017L;
	private RealRangeLong realRange;
	private String invalidInput;

	public LongValidator(String invalidInput, RealRangeLong realRange) {
		this.invalidInput = invalidInput;
		this.realRange = realRange;
	}

	@Override
	public void validate(Object value) throws InvalidValueException {
		try {
			if (!((String) value).isEmpty()) {
				Long parsedValue = Long.parseLong(((String) value));
				if (!realRange.contains(parsedValue)) {
					throw new InvalidValueException(invalidInput);
				}
			}
		} catch (NumberFormatException e) {
			throw new InvalidValueException(invalidInput);
		}
	}
}
