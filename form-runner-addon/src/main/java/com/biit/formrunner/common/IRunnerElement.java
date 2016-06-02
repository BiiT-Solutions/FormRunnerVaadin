package com.biit.formrunner.common;

import java.util.List;
import java.util.Locale;

import com.biit.formrunner.common.exceptions.PathDoesNotExist;
import com.vaadin.ui.Component;

public interface IRunnerElement extends Component {

	// New functions

	public String getName();

	public List<String> getPath();

	public IRunnerElement getElement(List<String> subList) throws PathDoesNotExist;

	public void setRelevance(boolean value);

	public boolean getRelevance();

	public boolean isValid();

	public void clear();

	public List<Result> getAnswers();

	public void setAnswers(List<String> list) throws UnsupportedOperationException;

	public void addElement(IRunnerElement element);

	public boolean isMandatory();

	public boolean isFilled();

	void addValueChangedListeners(FieldValueChanged listener);

	public void setTabIndex(int tabIndex);

	public void setLocale(Locale locale);

}
