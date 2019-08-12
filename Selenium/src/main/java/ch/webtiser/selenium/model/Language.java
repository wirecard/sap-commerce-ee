package ch.webtiser.selenium.model;

import ch.webtiser.selenium.util.PropertyHelper;

public enum Language {
	ENGLISH("en"), GERMAN("de");

	public static Language DEFAULT = Language.valueOf(PropertyHelper.loadProperties().getProperty("language.default"));

	private final String isoCode;

	Language(final String isoCode) {
		this.isoCode = isoCode;
	}

	public String isoCode() {
		return isoCode;
	}

	@Override
	public String toString() {
		return name();
	}
}
