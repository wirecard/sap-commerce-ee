package ch.webtiser.selenium.test;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.page.LoginPage;
import ch.webtiser.selenium.page.MainPage;
import ch.webtiser.selenium.util.DriverHelper;
import ch.webtiser.selenium.util.LoginHelper;
import ch.webtiser.selenium.util.PropertyHelper;
import ch.webtiser.selenium.util.enums.Environment;
import ch.webtiser.selenium.util.enums.OperatingSystem;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTestBase {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractTestBase.class);
	public static final String CART_NAME = "SELENIUMCART";
	final OperatingSystem os;
	final Environment env;
	final WebDriver driver;

	AbstractTestBase() {
		os = OperatingSystem.select();
		env = Environment.select();
		driver = DriverHelper.prepareWebDriver(os, env);
	}

	@Before
	public void setUp() {
		final String startsite = PropertyHelper.loadProperties().getProperty("firstPage");
		driver.get(startsite);
		loginToMainPage();
		clearCart();
		MainPage.goTo(driver, env);
	}

	@After
	public void tearDown() {
		LoginHelper.logout(driver, env.url());
		driver.close();
		driver.quit();
	}

	MainPage loginToMainPage() {
		return loginToMainPage(Customer.DEFAULT);
	}

	MainPage loginToMainPage(final Customer customer) {
		return LoginHelper.login(driver, env, customer);
	}

	LoginPage loadLoginPage() {
		return LoginHelper.loadLoginPage(driver, env);
	}

	public void clearCart() {
		/*final CartPage cartPage = CartPage.goTo(driver, env);
		cartPage.saveCartLink().click();
		cartPage.waitForLoad();
		final String currentUrl = driver.getCurrentUrl();
		if (currentUrl.endsWith(CartPage.DEFAULT_URL)) {
			cartPage.inputSaveCartName().sendKeys(CART_NAME);
			cartPage.waitForLoad();
			cartPage.buttonSaveNewCart().click();
			new WebDriverWait(driver, 10)
					.until(d -> cartPage.alertInfo().contains("wurde erfolgreich gespeichert"));
			LOG.info("New cart created.");
		} else {
			LOG.info("Removing saved cart.");
			final SavedCartsPage savedCartsPage = SavedCartsPage.goTo(driver, env);
			savedCartsPage.removeFirstSavedCart();
			clearCart(); //retry
		}*/
	}
}
