package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.model.payment.CreditCardMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CreditCard3DPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(CreditCard3DPage.class);

	private static final By ELEMENT_PASSWORD = By.id("password");
	private static final By ELEMENT_CONFIRM = By.xpath("//input[@value='authenticate']");

	private final Customer customer;


	public CreditCard3DPage(final WebDriver driver, final Environment env, final Customer customer) {
		super(driver, env);
		waitForLoad();
		this.customer = customer;
	}


	public static CreditCard3DPage goTo(final WebDriver driver, final Environment env, final Customer customer) {
		final CreditCard3DPage page = new CreditCard3DPage(driver, env, customer);
		LOG.debug("Loaded {}.", page);
		return page;
	}


	public void inputPasswordAndConfirm() {
		getPasswordInput().sendKeys(((CreditCardMethod)customer.getPayment()).getPassword3D());
		getConfirmButton().click();
	}


	private WebElement getConfirmButton() {
		return driver.findElement(ELEMENT_CONFIRM);
	}


	private WebElement getPasswordInput() {
		return driver.findElement(ELEMENT_PASSWORD);
	}
}
