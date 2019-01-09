package ch.webtiser.selenium.test;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.model.DeliveryAddress;
import ch.webtiser.selenium.model.Product;
import ch.webtiser.selenium.model.payment.*;
import ch.webtiser.selenium.page.*;
import ch.webtiser.selenium.util.DriverHelper;
import ch.webtiser.selenium.util.LoginHelper;
import ch.webtiser.selenium.util.PropertyHelper;
import ch.webtiser.selenium.util.enums.Environment;
import ch.webtiser.selenium.util.enums.OperatingSystem;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Driver;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CheckoutTest extends AbstractTestBase {
	private static final Product TEST_PRODUCT = Product.DEFAULT;
	private static final Customer TEST_CUSTOMER = Customer.DEFAULT;

	private void initProductDetailPage() {
		final ProductDetailPage pdp = ProductDetailPage.goTo(driver, env, TEST_PRODUCT);
		pdp.buttonAddToCart().click();
		pdp.waitForLoad();
	}

	private static void removeAllShipmentAddress(final WebDriver driver, final Environment env) {
	    final AdressBookPage adressBookPage = AdressBookPage.goTo(driver, env);
	    adressBookPage.removeAllAdresses();
        adressBookPage.waitForLoad();
    }

	@Override
	@Before
	public void setUp() {
		super.setUp();
		initProductDetailPage();
	}


	@Test
	public void creditCard3DCheckoutTest() {
		TEST_CUSTOMER.setPayment(CreditCardMethod.DEFAULT_3D);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
		if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }


		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		final CreditCard3DPage creditCard3DPage = new CreditCard3DPage(driver, env, TEST_CUSTOMER);
        creditCard3DPage.inputPasswordAndConfirm();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

    @Test
    public void creditCard3DFallbackCheckoutTest() {
        TEST_CUSTOMER.setPayment(CreditCardMethod.DEFAULT);
        final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

        chp.fillDeliveryMethod();
        chp.nextStep();
        chp.waitForLoad();

        chp.fillPayment();
        chp.nextStep();
        chp.waitForLoad();

        chp.fillTerms();
        chp.placeOrder();
        chp.waitForLoad();
        assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
    }

	@Test
	public void paypalCheckoutTest() {
		TEST_CUSTOMER.setPayment(PayPalMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		final PaypalPage payPalPage = new PaypalPage(driver, env, TEST_CUSTOMER);
		payPalPage.logInToPaypal();
		payPalPage.randomSelectPayment();
		payPalPage.confirmPurchase();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}


	@Test
	public void masterpassCheckoutTest() {
		TEST_CUSTOMER.setPayment(MasterpassMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		final MasterPassPage masterPassPage= new MasterPassPage(driver, env, TEST_CUSTOMER);
		masterPassPage.masterPassCheckout();
	}

	@Test
	public void alipayCheckoutTest() {
		TEST_CUSTOMER.setPayment(AlipayMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();

		AlipayPage ali = new AlipayPage(driver, env, TEST_CUSTOMER);
		ali.logInToAlipay();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

	@Test
	public void sofortCheckoutTest() {
		TEST_CUSTOMER.setPayment(SofortMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();

		SofortPage sofortPage = new SofortPage(driver, env, TEST_CUSTOMER);
		sofortPage.checkoutWithSofort();
		new WebDriverWait(driver, env.timeoutSeconds(),50).until(wd -> wd.getCurrentUrl().contains(env.baseUrl()));
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

	}

	@Test
	public void IdealCheckoutTest() {
		TEST_CUSTOMER.setPayment(IdealMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();

		IdealPage idealPage = new IdealPage(driver, env,TEST_CUSTOMER);
		idealPage.checkoutWithIdeal();
		new WebDriverWait(driver, env.timeoutSeconds(),50).until(wd -> wd.getCurrentUrl().contains(env.baseUrl()));
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

	}

	@Test
	public void sepaCheckoutTest() {
		TEST_CUSTOMER.setPayment(SepaMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();
		chp.fillSepaTerms();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

	@Test
	public void unionPayCheckoutTest() {
		TEST_CUSTOMER.setPayment(UnionPayMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

	@Test
	public void poiPaypheckoutTest() {
		TEST_CUSTOMER.setPayment(PoiPaiMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.fillDeliveryAddress();
        if(driver.getCurrentUrl().contains("delivery-address")) {
            chp.nextStep();
            chp.waitForLoad();
        }

		chp.fillDeliveryMethod();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillPayment();
		chp.nextStep();
		chp.waitForLoad();

		chp.fillTerms();
		chp.placeOrder();
		chp.waitForLoad();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
		assertNotNull(driver.findElement(By.className("order-invoice-details")));
		final List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@class,'order-invoice-details')]//span[@class='item-value']"));
		assertTrue(elements.size() == 3);
		for(final WebElement element : elements) {
			assertTrue(StringUtils.isNotEmpty(element.getText()));
		}
	}


}
