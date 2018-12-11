package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.model.payment.MasterpassMethod;
import ch.webtiser.selenium.model.payment.PayPalMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class MasterPassPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(MasterPassPage.class);

	private static final By ELEMENT_EMAIL = By.xpath("//input[@name='login']");
	private static final By ELEMENT_PASSWORD = By.xpath("//input[@name='password']");
	private static final By ELEMENT_SIGNIN = By.id("login-button");
	private static final By ELEMENT_WALLET= By.xpath("//div[@data-automation='MasterpassDESBX']");
	private static final By MASTERPASS_FRAME = By.id("MasterPass_frame");


	private final Customer customer;


	public MasterPassPage(final WebDriver driver, final Environment env, final Customer customer) {
		super(driver, env);
		waitForLoad();
		this.customer = customer;
	}



	public static MasterPassPage goTo(final WebDriver driver, final Environment env, final Customer customer) {
		final MasterPassPage page = new MasterPassPage(driver, env, customer);
		LOG.debug("Loaded {}.", page);
		return page;
	}

	public void masterPassCheckout() {
		final WebElement frame = waitForElement(MASTERPASS_FRAME);
		driver.switchTo().frame(frame);
		MasterpassMethod masterpassMethod = (MasterpassMethod) customer.getPayment();
		getWalletElement().click();
		waitForLoad();
		driver.switchTo().defaultContent();
		final WebElement frame1 = waitForElement(MASTERPASS_FRAME);
		driver.switchTo().frame(frame1);
		getEmailInput().sendKeys(masterpassMethod.getEmail());
		getPasswordInput().sendKeys(masterpassMethod.getPassword());
		getSignInButton().click();
		driver.switchTo().defaultContent();
	}

	private boolean isAvailable(By by) {
		return  !driver.findElements(by).isEmpty();
	}

	private WebElement waitForElement(final By by) {
		waitForLoad();
		new WebDriverWait(driver, env.timeoutSeconds(),50).until(wd -> isAvailable(by));
		return driver.findElement(by);
	}

	private WebElement getEmailInput() {
		return waitForElement(ELEMENT_EMAIL);
	}

	private WebElement getPasswordInput() {
		return waitForElement(ELEMENT_PASSWORD);
	}

	private WebElement getSignInButton() {
		return waitForElement(ELEMENT_SIGNIN);
	}

	private WebElement getWalletElement() {
		return waitForElement(ELEMENT_WALLET);
	}
}
