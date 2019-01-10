package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.PaymentFiller.*;
import ch.webtiser.selenium.model.payment.CreditCardMethod;
import ch.webtiser.selenium.model.payment.Payment;
import ch.webtiser.selenium.model.payment.PaymentType;
import ch.webtiser.selenium.util.enums.Environment;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class PaymentStep extends AbstractStep<Payment> {
    private static final String PAYMENT_TYPE_PREFIX= "wd-";
    private static final String DEFAULT_URL = "checkout/multi/wirecard/payment-method/add";

    private static final By ELEMENT_SAVED_CREDITCARDS = By.xpath("//div[@class='saved-payment-entry']//form[@name='wirecardPaymentDetailsForm']");
    private static final By ELEMENT_OPEN_SAVED_CREDITCARDS = By.xpath("//button[contains(@class,'js-saved-payments')]");
    private static final By ELEMENT_CLOSE_SAVED_CREDITCARDS = By.id("cboxClose");


    public PaymentStep(WebDriver driver, Environment env) {
        super(driver, env);
    }

    @Override
    public String url() {
        return null;
    }

    @Override
    public String title() {
        return null;
    }

    @Override
    public void fill(Payment object) {
        setPaymentType(object.getPaymentType());
        PaymentFiller filler = new DefaultPaymentFiller(driver, env);
        switch (object.getPaymentType()) {
            case CREDITCARD:
                    filler = new CreditCardPaymentFiller(driver, env);
                    break;
            case IDEAL:
                    filler = new IdealPaymentFiller(driver, env);
                    break;
            case SEPA:
                    filler = new SepaPaymentFiller(driver, env);
                    break;
            case UNIONPAY:
                    filler = new UnionPayPaymentFiller(driver, env);
                    break;
        }

        filler.fill(object);
    }

    @Override
    public void goTo() {
        driver.get(env.url() + DEFAULT_URL);
    }

    public void setPaymentType(final PaymentType paymentType) {
        getPaymentTypeRadio(paymentType.getCode().toString()).click();
    }

    private WebElement getPaymentTypeRadio(final String type) {
        return getElementById(By.id(PAYMENT_TYPE_PREFIX + type.toLowerCase()));
    }

    private boolean checkIfListContainsCreditCard(final CreditCardMethod creditCardMethod) {
        final List<WebElement> ccards = getSavedCreditCards();
        for(final WebElement ccard : ccards) {
            new WebDriverWait(driver, 1).until(wd -> !ccard.findElement(By.tagName("ul")).getText().isEmpty());
            final String paymentInfo = ccard.findElement(By.tagName("ul")).getText();
            final String[] infoLines = paymentInfo.split("\n");
            final String creditCardLine = infoLines[2].trim();
            final String ccardPrefix = creditCardLine.substring(0, 6);
            final String ccardSuffix = creditCardLine.substring(creditCardLine.length() - 4);
            if(creditCardMethod.getAccountNumber().startsWith(ccardPrefix) &&
                    StringUtils.endsWith(creditCardMethod.getAccountNumber(), ccardSuffix)) {
                return true;
            }
        }
        return false;
    }

    public boolean isCreditCardAvailable(final CreditCardMethod creditCardMethod) {
        openSavedCreditCardsPopup();
        final boolean result = checkIfListContainsCreditCard(creditCardMethod);
        closeSavedCreditCardPopup();
        return result;
    }

    public void openSavedCreditCardsPopup() {
        getOpenSavedCreditCardsButton().click();
    }

    public void closeSavedCreditCardPopup() {
        getCloseSavedCreditCardsButton().click();
    }

    public WebElement getOpenSavedCreditCardsButton() {
        return driver.findElement(ELEMENT_OPEN_SAVED_CREDITCARDS);
    }

    public WebElement getCloseSavedCreditCardsButton() {
        return driver.findElement(ELEMENT_CLOSE_SAVED_CREDITCARDS);
    }

    public List<WebElement> getSavedCreditCards() {
        return driver.findElements(ELEMENT_SAVED_CREDITCARDS);
    }


}
