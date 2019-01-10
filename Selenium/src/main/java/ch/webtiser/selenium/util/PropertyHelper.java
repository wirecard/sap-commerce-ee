package ch.webtiser.selenium.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyHelper {
	private static final Logger LOG = LoggerFactory.getLogger(PropertyHelper.class);
	private static final String PROPS_NAME = "selenium.properties";

	private PropertyHelper() {
		throw new UnsupportedOperationException("Utility class.");
	}

	public static Properties loadProperties() {
		final Properties props = new Properties();
		try (final InputStream is = PropertyHelper.class.getResourceAsStream("/" + PROPS_NAME)) {
			props.load(is);
		} catch (final IOException e) {
			LOG.error("Failed reading execution properties.", e);
		}
		return props;
	}
	public static String getProperty(final String key) {
		return loadProperties().getProperty(key);
	}
}
