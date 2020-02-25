package com.github.trytocatch.regexreplacer.ui;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class StrUtils {
	private static final String resource = "UIStrings";
	
	private static ResourceBundle getRB(){
		return ResourceBundle.getBundle(resource);
	}
	
	public static String getStr(String key){
		return getRB().getString(key);
	}
	public static String getStr(String key,Object... args){
		return MessageFormat.format(getStr(key), args);
	}
}
