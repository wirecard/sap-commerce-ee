package ch.webtiser.selenium.page;

import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CartPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(CartPage.class);
	public static final String DEFAULT_URL = "cart";

	public CartPage(final WebDriver driver, final Environment env) {
		super(driver, env);
		driver.get(env.url() + DEFAULT_URL);
		waitForLoad();
	}

	public static CartPage goTo(final WebDriver driver, final Environment env) {
		final CartPage page = new CartPage(driver, env);
		LOG.debug("Loaded {}.", page);
		return page;
	}

	public void cleanCart() {
		final List<WebElement> elements =  driver.findElements(By.className("js-execute-entry-action-button"));
		for(final WebElement element : elements) {
			//element.click();
			//waitForLoad();
		}
	}

	@Override
	public String toString() {
		return currentUrl;
	}

	public WebElement buttonCheckout() {
		return driver.findElement(By.className("js-continue-checkout-button"));
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
