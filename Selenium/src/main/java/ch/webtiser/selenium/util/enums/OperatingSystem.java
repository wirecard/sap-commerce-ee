package ch.webtiser.selenium.util.enums;

import ch.webtiser.selenium.util.PropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OperatingSystem {
	WINDOWS, MAC, UNIX, CI;

	private static final Logger LOG = LoggerFactory.getLogger(OperatingSystem.class);

	public static OperatingSystem select() {
		final String osName = System.getProperty("os.name");
		final String os = osName.toLowerCase();
		LOG.info("Operation-System: {}", osName);
		if (os.contains(WINDOWS.osName())) {
			return WINDOWS;
		} else if (os.contains(MAC.osName())) {
			return MAC;
			//if the System is Unix and set the SELENIUM_ENV to "ci"
			//its considered as Server, else local
		} else if(Environment.select().equals(Environment.CI)) {
			return CI;
		} else {
			return UNIX;
		}
	}

	public String osName() {
		return name().toLowerCase();
	}

	@Override
	public String toString() {
		return osName();
	}

	public String fileStoragePath() {
		return readProp("path.filestorage");
	}

	public String webDriverPath() {
		return readProp("path.webdriver");
	}

	private String readProp(final String prefix) {
		final String propKey = prefix + "." + osName();
		return PropertyHelper.loadProperties().getProperty(propKey);
	}
}
