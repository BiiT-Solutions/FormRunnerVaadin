package com.biit.form.runner.common;

public interface ValueModifier {

	String modifyToSave(String value);
	
	String modifyToLoad(String value);
}
