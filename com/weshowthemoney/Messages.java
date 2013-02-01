package com.weshowthemoney;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	public static  String BUNDLE_NAME = "com.weshowthemoney.messagesIT"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
		
	}
	

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
