package com.fiit.sipvs.XmlZadanie.helpers;

import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle("bundle.ResourceBundle", new Locale("en", "EN"));

	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
