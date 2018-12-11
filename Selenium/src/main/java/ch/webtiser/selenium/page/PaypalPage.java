package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.model.payment.PayPalMethod;
import ch.webtiser.selenium.page.step.PaymentStep;
import ch.webtiser.selenium.page.step.ReviewStep;
import ch.webtiser.selenium.page.step.ShipingMethodeStep;
import ch.webtiser.selenium.page.step.ShipmentStep;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class PaypalPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(PaypalPage.class);

	private static final By ELEMENT_EMAIL = By.xpath("//input[@id='email']");
	private static final By ELEMENT_PASSWORD = By.id("password");
	private static final By ELEMENT_GO_TO_LOGIN = By.xpath("//*[contains(@class,'baslLoginButtonContainer')]/a");
	private static final By ELEMENT_NEXT = By.xpath("//button[@id='btnNext']");
	private static final By ELEMENT_LOGIN = By.id("btnLogin");
	private static final By ELEMENT_FUNDING_SOURCE = By.className("fundingsource");
	private static final By ELEMENT_CONFIRM_FUNDING_SOURCE = By.cssSelector("#button .confirmButton");
	private static final By ELEMENT_CONFIRM_PURCHASE = By.id("confirmButtonTop");



	private final Customer customer;


	public PaypalPage(final WebDriver driver, final Environment env, final Customer customer) {
		super(driver, env);
		waitForLoad();
		this.customer = customer;
	}



	public static PaypalPage goTo(final WebDriver driver, final Environment env, final Customer customer) {
		final PaypalPage page = new PaypalPage(driver, env, customer);
		LOG.debug("Loaded {}.", page);
		return page;
	}

	public void logInToPaypal() {
		click(getGoToLoginButton());
		WebElement emailInput = getEmailInput();
		emailInput.click();
		emailInput.clear();
		sendValueCharByChar(emailInput, ((PayPalMethod)customer.getPayment()).getUsername());
		click(getNextButton());
		getPasswordInput().sendKeys(((PayPalMethod)customer.getPayment()).getPassword());
		click(getLoginButton());
	}

	public void randomSelectPayment() {
		final List<WebElement> inputs = getFundingSourcesInputs();
		final int randomInt = new Random().nextInt(inputs.size()-1);
		inputs.get(randomInt).click();
		click(getConfirmFundingSourcesButton());
	}

	public void confirmPurchase() {
		click(getConfirmPurchaseButton());
	}

	private List<WebElement> waitForElements(final By by) {
		waitForLoad();
		new WebDriverWait(driver, env.timeoutSeconds(),50).until(wd -> isAvailable(by));
		return driver.findElements(by);
	}
	private boolean isAvailable(By by) {
		return  !driver.findElements(by).isEmpty();
	}

	private WebElement waitForElement(final By by) {
		waitForLoad();
		new WebDriverWait(driver, env.timeoutSeconds(),50).until(wd -> isAvailable(by));
		return driver.findElement(by);
	}

	private void click(final WebElement toClick) {
		new WebDriverWait(driver, env.timeoutSeconds()).until(wd -> clickWithReturn(toClick));
	}

	private boolean clickWithReturn(final WebElement toClick) {
		try {
			toClick.click();
		} catch(org.openqa.selenium.WebDriverException e) {
			return false;
		}
		return true;
	}

	private WebElement getEmailInput() {
		new WebDriverWait(driver, 10).until((ExpectedCondition<Boolean>) wd -> driver.getTitle().contains("Loggen"));
		return waitForElement(ELEMENT_EMAIL);
	}

	private WebElement getPasswordInput() {
		return waitForElement(ELEMENT_PASSWORD);
	}

	private WebElement getGoToLoginButton() {
		return  waitForElement(ELEMENT_GO_TO_LOGIN);
	}

	private WebElement getLoginButton() {
		return  waitForElement(ELEMENT_LOGIN);
	}

	private WebElement getNextButton() {
		return  waitForElement(ELEMENT_NEXT);
	}

	private List<WebElement> getFundingSourcesInputs() {
		return  waitForElements(ELEMENT_FUNDING_SOURCE);
	}

	private WebElement getConfirmFundingSourcesButton() {
		return  waitForElement(ELEMENT_CONFIRM_FUNDING_SOURCE);
	}


	private WebElement getConfirmPurchaseButton() {
		return  waitForElement(ELEMENT_CONFIRM_PURCHASE);
	}
	protected void sendValueCharByChar(WebElement element, String value) {
		Arrays.stream(value.split(""))
				.forEach(element::sendKeys);
	}
}
