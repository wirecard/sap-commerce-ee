package ch.webtiser.selenium.util;

import ch.webtiser.selenium.util.enums.Environment;
import ch.webtiser.selenium.util.enums.OperatingSystem;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class DriverHelper {
	private static final Logger LOG = LoggerFactory.getLogger(DriverHelper.class);
	private static final int DEFAULT_W = 1920;
	private static final int DEFAULT_H = 1080;

	private DriverHelper() {
		throw new UnsupportedOperationException("Utility class.");
	}

	public static WebDriver prepareWebDriver(final OperatingSystem os, final Environment env) {
		final Properties props = PropertyHelper.loadProperties();
		final String webdriverPath = props.getProperty("path.webdriver." + os.osName());
		System.setProperty("webdriver.chrome.driver", webdriverPath);
		LOG.info("Trying to load webdriver from path {}.", webdriverPath);
		final Optional<WebDriver> maybeDriver = createWebDriver(env);
		final Optional<WebDriver.Options> maybeOptions = maybeDriver.map(WebDriver::manage);
		maybeOptions.ifPresent(opts -> initializeDriverOpts(opts, env));
		return maybeDriver.orElseThrow(IllegalStateException::new);
	}

	private static void initializeDriverOpts(final WebDriver.Options opts, final Environment env) {
		opts.timeouts().implicitlyWait(env.timeoutSeconds(), TimeUnit.SECONDS);
		if (!env.isRunningOnGit()) {
			opts.window().setSize(windowDimensions());
		}
	}

	private static Dimension windowDimensions() {
		final Properties props = PropertyHelper.loadProperties();
		// Use the same initial dimension for every test
		final String propW = props.getProperty("defaultWidth");
		final String propH = props.getProperty("defaultHeight");
		final int w = propW != null ? Integer.parseInt(propW) : DEFAULT_W;
		final int h = propH != null ? Integer.parseInt(propH) : DEFAULT_H;
		return new Dimension(w, h);
	}

	private static Optional<WebDriver> createWebDriver(final Environment env) {
		final String remoteIp = System.getProperty("SELENIUM_REMOTEIP");
		if (StringUtils.isNotEmpty(remoteIp)) {
			final String remoteEnv = "http://" + remoteIp + ":4444/wd/hub";
			try {
				final URL url = new URL(remoteEnv);
				return Optional.of(new RemoteWebDriver(url, DesiredCapabilities.chrome()));
			} catch (final MalformedURLException e) {
				LOG.error("Incorrect format of URL from properties.", e);
				return Optional.empty();
			}
		} else {
			final ChromeDriver driver = env.isRunningOnGit() ?
					new ChromeDriver(getChromeOptions()) : new ChromeDriver();
			return Optional.ofNullable(driver);
		}
	}

	private static ChromeOptions getChromeOptions() {
		final ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1920,1080");
		//options.addArguments("--disable-gpu");
		options.addArguments("--no-sandbox");
		//options.addArguments("--disable-extensions");
		options.setExperimentalOption("useAutomationExtension", false);
		//options.addArguments("--proxy-server='direct://'");
		//options.addArguments("--proxy-bypass-list=*");
		options.addArguments("--start-maximized");
		options.addArguments("--headless");
		options.addArguments("--disable-web-security");
		//options.setExperimentalOption("profile.default_content_setting_values.cookies", 2);
		options.setCapability("acceptInsecureCerts", true);
		return options;
	}
}
