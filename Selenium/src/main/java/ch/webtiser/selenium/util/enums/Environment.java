package ch.webtiser.selenium.util.enums;

import ch.webtiser.selenium.model.Language;
import ch.webtiser.selenium.util.PropertyHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public enum Environment {
	PRODUCTION,
	INTEGRATION,
	LOCAL,
	CI,
	DUMMY;

	public static final Environment DEFAULT = LOCAL;
	private static final Logger LOG = LoggerFactory.getLogger(Environment.class);
	private static final int DEFAULT_TIMEOUT_SECONDS = 30;

	public static Environment select() {
		final String sysProp = System.getProperty("SELENIUM_ENV");
		LOG.info("Selecting environment {}.", sysProp);
		if (StringUtils.isNotEmpty(sysProp)) {
			return Arrays.stream(values())
					.filter(env -> env.name().equalsIgnoreCase(sysProp))
					.findFirst().orElse(DEFAULT);
		} else {
			LOG.info("No environment set. Applying fallback to default: {}.", DEFAULT);
			return DEFAULT;
		}
	}

	public String url() {
		return url(Language.DEFAULT);
	}

	public String url(final Language lang) {
		return readProp("url." + lang.isoCode());
	}

	public String baseUrl() {
		return readProp("baseUrl");
	}

	public int timeoutSeconds() {
		return Integer.valueOf(
				Optional.ofNullable(readProp("timeout"))
						.orElse(String.valueOf(DEFAULT_TIMEOUT_SECONDS))
		);
	}

	private String readProp(final String prefix) {
		final String propKey = prefix + "." + envName();
		return PropertyHelper.loadProperties().getProperty(propKey);
	}

	public String envName() {
		return name().toLowerCase();
	}

	public boolean isRunningOnGit() {
		return Environment.CI == this;
	}

	@Override
	public String toString() {
		return envName();
	}
}
