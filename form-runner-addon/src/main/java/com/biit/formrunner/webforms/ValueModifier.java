package com.biit.formrunner.webforms;

public interface ValueModifier {

	String modifyToSave(String value);
	
	String modifyToLoad(String value);
}
