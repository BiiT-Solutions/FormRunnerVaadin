package com.biit.formrunner.common;

public interface ValueModifier {

	String modifyToSave(String value);
	
	String modifyToLoad(String value);
}
