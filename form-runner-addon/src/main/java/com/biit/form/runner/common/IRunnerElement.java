package com.biit.form.runner.common;

import java.util.List;
import java.util.Locale;

import com.biit.form.runner.common.exceptions.PathDoesNotExist;
import com.vaadin.ui.Component;

public interface IRunnerElement extends Component {

	public String getName();

	public List<String> getPath();

	public IRunnerElement getElement(List<String> subList) throws PathDoesNotExist;

	public void setRelevance(boolean value);

	public boolean getRelevance();

	public boolean isValid();

	public void clear();

	public List<Result> getAnswers();

	public void setAnswers(List<String> answers) throws UnsupportedOperationException;

	public void addElement(IRunnerElement element);

	public boolean isMandatory();

	public boolean isFilled();

	void addValueChangedListeners(FieldValueChanged listener);

	public void setTabIndex(int tabIndex);

	public void setLocale(Locale locale);

	public void setDescription(String string);

	/** Hidden elements are not used in the form flow, but included in the json */
	public boolean isHidden();

	public void setHidden(boolean hidden);

}
