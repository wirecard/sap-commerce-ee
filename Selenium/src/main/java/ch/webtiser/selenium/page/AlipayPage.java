package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.model.payment.AlipayMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AlipayPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(AlipayPage.class);

	private static final By ELEMENT_SWITCH_TO_ACCOUNT = By.cssSelector("#J_viewSwitcher a.switch-tip-btn");
	private static final By ELEMENT_USERNAME = By.id("J_tLoginId");
	private static final By ELEMENT_PASSWORD = By.id("payPasswd_rsainput");
	private static final By ELEMENT_LOGIN_BUTTON = By.id("J_newBtn");
	private static final By ELEMENT_TERMS = By.id("J_foreAgreement");


	private final Customer customer;


	public AlipayPage(final WebDriver driver, final Environment env, final Customer customer) {
		super(driver, env);
		waitForLoad();
		this.customer = customer;
	}


	public static AlipayPage goTo(final WebDriver driver, final Environment env, final Customer customer) {
		final AlipayPage page = new AlipayPage(driver, env, customer);
		LOG.debug("Loaded {}.", page);
		return page;
	}

	public void logInToAlipay() {
		getSwitchToAccountButton().click();
		getUsernameInput().sendKeys(((AlipayMethod)customer.getPayment()).getUsername());
		getPasswordInput().sendKeys(((AlipayMethod)customer.getPayment()).getPassword());
		getTermsCheckbox().click();
		getLoginButton().click();
	}


	private WebElement getUsernameInput() {
		return driver.findElement(ELEMENT_USERNAME);
	}

	private WebElement getPasswordInput() {
		return driver.findElement(ELEMENT_PASSWORD);
	}

	private WebElement getLoginButton() {
		return driver.findElement(ELEMENT_LOGIN_BUTTON);
	}

	private WebElement getTermsCheckbox() {
		return driver.findElement(ELEMENT_TERMS);
	}

	private WebElement getSwitchToAccountButton() {
		return driver.findElement(ELEMENT_SWITCH_TO_ACCOUNT);
	}
}
