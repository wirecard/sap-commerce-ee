package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.model.payment.SofortMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IdealPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(IdealPage.class);

	private static final By ELEMENT_CONFIRM_TRANSACTION = By.className("btnLink");


	private final Customer customer;


	public IdealPage(final WebDriver driver, final Environment env, final Customer customer) {
		super(driver, env);
		waitForLoad();
		this.customer = customer;
	}


	public static IdealPage goTo(final WebDriver driver, final Environment env, final Customer customer) {
		final IdealPage page = new IdealPage(driver, env, customer);
		LOG.debug("Loaded {}.", page);
		return page;
	}

	public void checkoutWithIdeal() {
		getConfitmTransactionButton().click();
	}


	private WebElement getConfitmTransactionButton() {
		return driver.findElement(ELEMENT_CONFIRM_TRANSACTION);
	}

}
