package ch.webtiser.selenium.page;

import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(MainPage.class);

	public MainPage(final WebDriver driver, final Environment env) {
		super(driver, env);
	}

	public static MainPage goTo(final WebDriver driver, final Environment env) {
		final MainPage page = new MainPage(driver, env);
		LOG.debug("Loaded {}.", page);
		return page;
	}

	@Override
	public String url() {
		return currentUrl;
	}

	@Override
	public String title() {
		return pageTitle;
	}
}
