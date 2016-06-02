package com.biit.formrunner.orbeon;

import java.util.List;
import java.util.Set;

import com.biit.form.submitted.ISubmittedObject;
import com.biit.form.submitted.ISubmittedQuestion;

public class MockSubmittedQuestion implements ISubmittedQuestion {

	private String pathName;

	@Override
	public String getTag() {
		return null;
	}

	@Override
	public void setTag(String tag) {
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public void setText(String text) {
	}

	@Override
	public ISubmittedObject getParent() {
		return null;
	}

	@Override
	public void setParent(ISubmittedObject parent) {
	}

	@Override
	public void addChild(ISubmittedObject child) {
	}

	@Override
	public List<ISubmittedObject> getChildren() {
		return null;
	}

	@Override
	public void setChildren(List<ISubmittedObject> children) {
	}

	@Override
	public ISubmittedObject getChild(Class<?> type, String tag) {
		return null;
	}

	@Override
	public List<ISubmittedObject> getChildren(Class<?> type) {

		return null;
	}

	@Override
	public String getPathName() {
		return pathName;
	}

	@Override
	public List<String> getPath() {
		return null;
	}

	@Override
	public Integer getIndex(ISubmittedObject child) {
		return null;
	}

	@Override
	public int compareTo(ISubmittedObject arg0) {
		return 0;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	@Override
	public ISubmittedObject getChild(List<String> subList) {
		return null;
	}

	@Override
	public ISubmittedObject getChild(String pathstring) {
		return null;
	}

	@Override
	public void addAnswer(String value) {
	}

	@Override
	public Set<String> getAnswers() {
		return null;
	}

	@Override
	public void setAnswers(Set<String> answers) {
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

}
