package ch.webtiser.selenium.test;

import ch.webtiser.selenium.model.Customer;
import ch.webtiser.selenium.model.DeliveryAddress;
import ch.webtiser.selenium.model.Product;
import ch.webtiser.selenium.model.payment.*;
import ch.webtiser.selenium.page.*;
import ch.webtiser.selenium.util.enums.Environment;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.Assert.*;

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

        chp.completeDeliveryAddressStep();
        chp.completeDeliveryMethodStep();
        chp.completePaymentStep(false);

		final CreditCard3DPage creditCard3DPage = new CreditCard3DPage(driver, env, TEST_CUSTOMER);
        creditCard3DPage.inputPasswordAndConfirm();

		chp.completeSummaryStep();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

    @Test
    public void creditCard3DFallbackCheckoutTest() {
        TEST_CUSTOMER.setPayment(CreditCardMethod.DEFAULT);
        final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.completeCheckoutWithoutAuthStep(false);
        assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
    }

	@Test
	public void paypalCheckoutTest() {
		TEST_CUSTOMER.setPayment(PayPalMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);

		chp.completeDeliveryAddressStep();
		chp.completeDeliveryMethodStep();
		chp.completePaymentStep(false);

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
        chp.completeCheckoutWithoutAuthStep(false);

		AlipayPage ali = new AlipayPage(driver, env, TEST_CUSTOMER);
		ali.logInToAlipay();
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

	@Test
	public void sofortCheckoutTest() {
		TEST_CUSTOMER.setPayment(SofortMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.completeCheckoutWithoutAuthStep(false);

		SofortPage sofortPage = new SofortPage(driver, env, TEST_CUSTOMER);
		sofortPage.checkoutWithSofort();
		new WebDriverWait(driver, env.timeoutSeconds(),50).until(wd -> wd.getCurrentUrl().contains(env.baseUrl()));
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

	}

	@Test
	public void IdealCheckoutTest() {
		TEST_CUSTOMER.setPayment(IdealMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.completeCheckoutWithoutAuthStep(false);

		IdealPage idealPage = new IdealPage(driver, env,TEST_CUSTOMER);
		idealPage.checkoutWithIdeal();
		new WebDriverWait(driver, env.timeoutSeconds(),50).until(wd -> wd.getCurrentUrl().contains(env.baseUrl()));
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

	}

	@Test
	public void sepaCheckoutTest() {
		TEST_CUSTOMER.setPayment(SepaMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.completeDeliveryAddressStep();
        chp.completeDeliveryMethodStep();
        chp.completePaymentStep(false);
        chp.fillSepaTerms();
        chp.completeSummaryStep();

		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

	@Test
	public void unionPayCheckoutTest() {
		TEST_CUSTOMER.setPayment(UnionPayMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.completeCheckoutWithoutAuthStep(false);
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
	}

	@Test
	public void poiPaypheckoutTest() {
		TEST_CUSTOMER.setPayment(PoiPaiMethod.DEFAULT);
		final CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
		chp.completeCheckoutWithoutAuthStep(false);
		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));
		assertNotNull(driver.findElement(By.className("order-invoice-details")));
		final List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@class,'order-invoice-details')]//span[@class='item-value']"));
		assertTrue(elements.size() == 3);
		for(final WebElement element : elements) {
			assertTrue(StringUtils.isNotEmpty(element.getText()));
		}
	}

	@Test
	public void savePaymentCreditCardTest() {
		TEST_CUSTOMER.setPayment(CreditCardMethod.DEFAULT);

		final AdressBookPage adressBookPage = new AdressBookPage(driver, env);
		adressBookPage.removeAllAdresses();

		final PaymentDetailsPage paymentDetailsPage = PaymentDetailsPage.goTo(driver, env);
		paymentDetailsPage.removeAllPayments();

		AddAddressPage addAddressPage = new AddAddressPage(driver, env);
		addAddressPage.addAddresse(TEST_CUSTOMER.getDeliveryAddress());
		addAddressPage = AddAddressPage.goTo(driver,env);
		addAddressPage.addAddresse(DeliveryAddress.SECOND_ADDRESS);

		CheckoutPage chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.completePaymentStep(true);
        chp.completeSummaryStep();

		assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

		initProductDetailPage();
		chp = CheckoutPage.goTo(driver, env, TEST_CUSTOMER);
		chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
		chp.completeDeliveryMethodStep();
		chp.getPaymentStep().setPaymentType(PaymentType.CREDITCARD);

		assertTrue(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) TEST_CUSTOMER.getPayment()));

        TEST_CUSTOMER.setDeliveryAddress(DeliveryAddress.SECOND_ADDRESS);
        TEST_CUSTOMER.setPayment(CreditCardMethod.DEFAULT_3D);
        chp = CheckoutPage.goTo(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.completePaymentStep(true);

        final CreditCard3DPage creditCard3DPage = new CreditCard3DPage(driver, env, TEST_CUSTOMER);
        creditCard3DPage.inputPasswordAndConfirm();

        chp.completeSummaryStep();

        assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

        initProductDetailPage();
        chp = CheckoutPage.goTo(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.getPaymentStep().setPaymentType(PaymentType.CREDITCARD);

        assertTrue(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) TEST_CUSTOMER.getPayment()));
        new WebDriverWait(driver, 1).until(wd -> driver.findElement(By.id("colorbox")).getCssValue("display").equals("none"));
		assertFalse(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) CreditCardMethod.DEFAULT));

        TEST_CUSTOMER.setPayment(CreditCardMethod.DEFAULT);
        chp = CheckoutPage.goTo(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.completePaymentStep(true);
        chp.completeSummaryStep();

        assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

        initProductDetailPage();
        chp = CheckoutPage.goTo(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.getPaymentStep().setPaymentType(PaymentType.CREDITCARD);

        assertTrue(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) TEST_CUSTOMER.getPayment()));
        new WebDriverWait(driver, 1).until(wd -> driver.findElement(By.id("colorbox")).getCssValue("display").equals("none"));
        assertTrue(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) CreditCardMethod.DEFAULT));

        TEST_CUSTOMER.setDeliveryAddress(DeliveryAddress.DEFAULT);
        chp = CheckoutPage.goTo(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.getPaymentStep().setPaymentType(PaymentType.CREDITCARD);
		assertFalse(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) CreditCardMethod.DEFAULT_3D));

		new WebDriverWait(driver, 1).until(wd -> driver.findElement(By.id("colorbox")).getCssValue("display").equals("none"));
        assertTrue(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) CreditCardMethod.DEFAULT));

        TEST_CUSTOMER.setPayment(CreditCardMethod.DEFAULT_AE);
        chp = new CheckoutPage(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.completePaymentStep(false);
        chp.completeSummaryStep();

        assertTrue(driver.getCurrentUrl().contains("orderConfirmation"));

		initProductDetailPage();
        chp = CheckoutPage.goTo(driver, env, TEST_CUSTOMER);
        chp.getShipmentStep().selectAddressFromAddressBook(TEST_CUSTOMER.getDeliveryAddress());
        chp.completeDeliveryMethodStep();
        chp.getPaymentStep().setPaymentType(PaymentType.CREDITCARD);

        assertFalse(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) CreditCardMethod.DEFAULT_3D));
        new WebDriverWait(driver, 1).until(wd -> driver.findElement(By.id("colorbox")).getCssValue("display").equals("none"));
        assertTrue(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) CreditCardMethod.DEFAULT));
        new WebDriverWait(driver, 1).until(wd -> driver.findElement(By.id("colorbox")).getCssValue("display").equals("none"));
        assertFalse(chp.getPaymentStep().isCreditCardAvailable((CreditCardMethod) CreditCardMethod.DEFAULT_AE));

    }


}
