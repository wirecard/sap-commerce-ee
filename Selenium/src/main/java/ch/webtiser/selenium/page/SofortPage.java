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


public class SofortPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(SofortPage.class);

	private static final By ELEMENT_COUNTRY_OF_BANK = By.id("MultipaysSessionSenderCountryId");
	private static final By ELEMENT_BANK = By.id("SenderBank");
	private static final By ELEMENT_BANKCODE = By.id("BankCodeSearch");
	private static final By ELEMENT_CONTRACT_NUMBER = By.id("BackendFormLOGINNAMEUSERID");
	private static final By ELEMENT_PASSWORD = By.id("BackendFormUSERPIN");
	private static final By ELEMENT_TAN = By.id("BackendFormTan");
	private static final By ELEMENT_NEXT = By.cssSelector("#WizardForm button");
	private static final By ELEMENT_BACK = By.className("redirect-button");
	private static final String ELEMENT_ACCOUNT_SELECTION_PREFIX = "account-";


	private final Customer customer;


	public SofortPage(final WebDriver driver, final Environment env, final Customer customer) {
		super(driver, env);
		waitForLoad();
		this.customer = customer;
	}


	public static SofortPage goTo(final WebDriver driver, final Environment env, final Customer customer) {
		final SofortPage page = new SofortPage(driver, env, customer);
		LOG.debug("Loaded {}.", page);
		return page;
	}

	public void checkoutWithSofort() {
		selectBank();
		clickNext();
		login();
		clickNext();
		selectAccount();
		clickNext();
		inputTan();
		clickNext();
		clickBackToMerchant();
	}


	public void selectBank(){
		final SofortMethod sofortMethod = (SofortMethod) customer.getPayment();
		getCountryOfBankSelect().selectByValue(sofortMethod.getCountryOfBank().toString());
		getBankCodeInput().sendKeys(sofortMethod.getBankName());
	}

	public void login() {
		final SofortMethod sofortMethod = (SofortMethod) customer.getPayment();
		getContractNumberSelect().sendKeys(sofortMethod.getContractNumber());
		getPasswordSelect().sendKeys(sofortMethod.getPassword());
	}

	public void selectAccount() {
		final SofortMethod sofortMethod = (SofortMethod) customer.getPayment();
		getSelectAccountInput(sofortMethod.getAccountId()).click();
	}

	public void inputTan() {
		final SofortMethod sofortMethod = (SofortMethod) customer.getPayment();
		getTanInput().sendKeys(sofortMethod.getTan());
	}

	public void clickNext() {
		getNextButton().click();
		waitForLoad();
	}

	public void clickBackToMerchant() {
		getBackToButton().click();
		waitForLoad();
	}

	private WebElement getSelectAccountInput(final int id) {
		return driver.findElement(By.id(ELEMENT_ACCOUNT_SELECTION_PREFIX + id));
	}

	private Select getCountryOfBankSelect() {
		return new Select(driver.findElement(ELEMENT_COUNTRY_OF_BANK));
	}

	private Select getBankSelect() {
		return new Select(driver.findElement(ELEMENT_BANK));
	}

	private WebElement getBankCodeInput() {
		return driver.findElement(ELEMENT_BANKCODE);
	}

	private WebElement getTanInput() {
		return driver.findElement(ELEMENT_TAN);
	}


	private WebElement getContractNumberSelect() {
		return driver.findElement(ELEMENT_CONTRACT_NUMBER);
	}

	private WebElement getPasswordSelect() {
		return driver.findElement(ELEMENT_PASSWORD);
	}

	private WebElement getNextButton() {
		return driver.findElement(ELEMENT_NEXT);
	}

	private WebElement getBackToButton() {
		return driver.findElement(ELEMENT_BACK);
	}

}
