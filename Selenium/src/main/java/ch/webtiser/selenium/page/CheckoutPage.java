package ch.webtiser.selenium.page;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.page.step.*;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CheckoutPage extends AbstractPage {
	private static final Logger LOG = LoggerFactory.getLogger(CheckoutPage.class);
	private static final String DEFAULT_URL = "checkout";


	private static final By ELEMENT_SEPATERMS = By.id("sepaMandateChkConditions");
	private static final By ELEMENT_CONFIRMBUTTON = By.id("sepaMandateButton");
    private static final By ELEMENT_ERRORMESSAGE = By.cssSelector("global-alerts.alert");
	private static final By ELEMENT_SAVE_PAYMENT= By.id("savePaymentMethod");



	private ShipmentStep shipmentStep;
	private ShipingMethodeStep shipingMethodeStep;
	private PaymentStep paymentStep;
	private ReviewStep reviewStep;

	private final Customer customer;


	public CheckoutPage(final WebDriver driver, final Environment env, final Customer customer) {
		super(driver, env);
		driver.get(env.url() + DEFAULT_URL);
		waitForLoad();
		this.customer = customer;

		shipmentStep = new ShipmentStep(driver, env);
		shipingMethodeStep = new ShipingMethodeStep(driver, env);
		paymentStep = new PaymentStep(driver, env);
		reviewStep = new ReviewStep(driver, env);
	}

	public static CheckoutPage goTo(final WebDriver driver, final Environment env, final Customer customer) {
		final CheckoutPage page = new CheckoutPage(driver, env, customer);
		LOG.debug("Loaded {}.", page);
		return page;
	}


	public void fillDeliveryAddress() {
       shipmentStep.fill(customer.getDeliveryAddress());
        waitForLoad();
	}

	public void completeDeliveryAddressStep() {
		fillDeliveryAddress();
		nextStep();
		waitForLoad();
	}

	public void completeDeliveryMethodeStep() {
		fillDeliveryMethod();
		nextStep();
		waitForLoad();
	}
	public void completePaymentStep(final boolean savePayment) {
		fillPayment();
		if(savePayment) {
			savePayment();
		}
		nextStep();
		waitForLoad();
	}

	public void completeSummaryStep() {
		fillTerms();
		placeOrder();
		waitForLoad();
	}

	public void completeCheckoutWithoutAuthStep(final boolean savePayment) {
        completeDeliveryAddressStep();
        completeDeliveryMethodeStep();
        completePaymentStep(savePayment);
        completeSummaryStep() ;

    }


	public void fillDeliveryMethod() {
		shipingMethodeStep.fill(customer.getDeliveryMethod());
	}

	public void fillPayment() {
		paymentStep.fill(customer.getPayment());
	}

	public void fillTerms() {
		reviewStep.fill(true);
	}

	public void fillSepaTerms(){
		driver.findElement(ELEMENT_SEPATERMS).click();
		driver.findElement(ELEMENT_CONFIRMBUTTON).click();
	}

	public String getErrorMessage() {
	    final String errorText = getErrorMessageElement().getText();
	    return errorText.contains("<button>") ? errorText.substring(errorText.indexOf("</button>") + "</button>".length()) : errorText;
    }

	public void nextStep() {
		driver.findElement(By.className("checkout-next")).click();
	}

	public void placeOrder() {
		reviewStep.placeOrder();
	}

	public void savePayment() {
		getSavePaymentCheckbox().click();
	}

	public WebElement getSavePaymentCheckbox() {
		return driver.findElement(ELEMENT_SAVE_PAYMENT);
	}

	public WebElement getErrorMessageElement() {
		return driver.findElement(ELEMENT_ERRORMESSAGE);
	}

    public ShipmentStep getShipmentStep() {
        return shipmentStep;
    }

    public ShipingMethodeStep getShipingMethodeStep() {
        return shipingMethodeStep;
    }

    public PaymentStep getPaymentStep() {
        return paymentStep;
    }

    public ReviewStep getReviewStep() {
        return reviewStep;
    }
}
