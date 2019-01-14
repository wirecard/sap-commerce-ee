package ch.webtiser.selenium.page;

import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class PaymentDetailsPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(PaymentDetailsPage.class);
	private final String currentUrl;
	private final String pageTitle;
    public static final String DEFAULT_URL ="my-account/payment-details";

	private final By ELEMENT_REMOVE = By.className("removePaymentDetailsButton");
	private final By ELEMENT_REMOVE_ADRESS_BUTTON = By.xpath("//div[@id='cboxLoadedContent']//div[@class='account-address-removal-popup']//button[contains(@class, 'paymentsDeleteBtn')]");

	public PaymentDetailsPage(final WebDriver driver, final Environment env) {
		super(driver, env);
        driver.get(env.url() + DEFAULT_URL);
		waitForLoad();
		currentUrl = driver.getCurrentUrl();
		pageTitle = driver.getTitle();
	}

	public static PaymentDetailsPage goTo(final WebDriver driver, final Environment env) {
		final PaymentDetailsPage page = new PaymentDetailsPage(driver, env);
		return page;
	}

	public void removeAllPayments() {
		WebElement icon = getRemoveIcon();
		for(;icon != null; icon = getRemoveIcon()) {
			icon.click();
			getRemoveButton().click();
			waitForLoad(driver, env);
		}
	}


	public WebElement getRemoveIcon() {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		WebElement icon = null;
		if(!driver.findElements(ELEMENT_REMOVE).isEmpty()) {
			icon = driver.findElement(ELEMENT_REMOVE);
		}
		driver.manage().timeouts().implicitlyWait(env.timeoutSeconds(), TimeUnit.SECONDS);
		return icon;
	}

	public WebElement getRemoveButton() {
		return driver.findElement(ELEMENT_REMOVE_ADRESS_BUTTON);
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
