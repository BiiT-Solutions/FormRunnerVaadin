package com.biit.formrunner.webforms;

import java.util.List;
import java.util.Locale;

import com.biit.form.result.FormResult;
import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.biit.webforms.persistence.entity.Form;
import com.vaadin.ui.Component;

public interface IWebformsRunner extends Component {

	void loadForm(Form form);

	void setLocale(Locale locale);

	void setImagesEnabled(boolean imagesEnabled);

	void setPhoneRegex(String phoneValidatorRegex);

	void setTextRegex(String textValidatorRegex);

	void setEmailRegex(String emailValidatorRegex);

	void setPostalCodeRegex(String postalCodeValidatorRegex);

	void setInvalidCaption(String translation);

	void setRequiredCaption(String translation);

	void setTabIndexDelta(int i);

	void setValueNotSaved(boolean b);

	void loadFormResult(FormResult formResult);

	boolean isValueNotSaved();

	void setAnswers(List<String> path, List<String> answers) throws PathDoesNotExist;

	boolean isValid();

	FormResult getFormResult();

}
