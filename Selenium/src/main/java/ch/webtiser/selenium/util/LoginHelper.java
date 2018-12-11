package ch.webtiser.selenium.util;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.page.LoginPage;
import ch.webtiser.selenium.page.MainPage;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginHelper {
	private static final Logger LOG = LoggerFactory.getLogger(LoginHelper.class);

	private LoginHelper() {
		throw new UnsupportedOperationException("Utility class.");
	}

	public static MainPage login(final WebDriver driver, final Environment env, final Customer customer) {
		final LoginPage loginPage = new LoginPage(driver, env);
		final MainPage mainPage = loginPage.loginCustomer(customer);
		LOG.debug("Successfully logged in '{}' at {}.", customer, env.url());
		return mainPage;
	}

	public static LoginPage loadLoginPage(final WebDriver driver, final Environment env) {
		final LoginPage loginPage = new LoginPage(driver, env).loadLoginPage();
		LOG.debug("Successfully logged LoginPage.", env.url());
		return loginPage;
	}

	public static void logout(final WebDriver driver, final String url) {
		driver.get(url + "/logout");
		LOG.debug("Logged out of {}.", url);
	}
}
