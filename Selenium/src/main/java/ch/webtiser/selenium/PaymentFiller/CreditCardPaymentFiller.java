package ch.webtiser.selenium.PaymentFiller;

import ch.webtiser.selenium.model.payment.CreditCardMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class CreditCardPaymentFiller extends AbstractPaymentFiller<CreditCardMethod> {

    private static final By ELEMENT_PROVIDER = By.id("card_type");
    private static final By ELEMENT_ACCOUNT_NUMBER = By.id("account_number");
    private static final By ELEMENT_EXPIRATION_DATE_MONTH = By.id("expiration_month_list");
    private static final By ELEMENT_EXPIRATION_DATE_YEAR = By.id("expiration_year_list");
    private static final By ELEMENT_CARD_HOLDER = By.id("Card-holder");
    private static final By ELEMENT_SECURITY_CODE= By.id("card_security_code");

    public CreditCardPaymentFiller(WebDriver driver, Environment env) {
        super(driver, env);
    }


    @Override
    public void fill(final CreditCardMethod payment) {
        switchToFrame(payment);
        getAccountNumberInput().sendKeys(payment.getAccountNumber());
        getExpirationDateMonthSelect().selectByValue(payment.getExpirationDateMont());
        getExpirationDateYearSelect().selectByValue("20"+payment.getExpirationDateYear());
        getCardHolderInput().sendKeys(payment.getCardHolder());
        getSecurityCodeInput().sendKeys(payment.getSecurityCode());
        driver.findElement(By.id("select2-chosen-1")).click();
        getProvider(payment.getCardProvider().getCode()).click();
        driver.switchTo().defaultContent();
    }

    private WebElement getProvider(final String providerName) {
        return driver.findElement(By.xpath("//div[@class= 'select2-result-label']//span[contains(text(), '" + providerName + "')]/.."));
    }

    private WebElement getAccountNumberInput() {
        return driver.findElement(ELEMENT_ACCOUNT_NUMBER);
    }
    private Select getExpirationDateMonthSelect() {
        return new Select(driver.findElement(ELEMENT_EXPIRATION_DATE_MONTH));
    }
    private Select getExpirationDateYearSelect() {
        return new Select(driver.findElement(ELEMENT_EXPIRATION_DATE_YEAR));
    }
    private WebElement getCardHolderInput() {
        return driver.findElement(ELEMENT_CARD_HOLDER);
    }

    private WebElement getSecurityCodeInput() {
        return driver.findElement(ELEMENT_SECURITY_CODE);
    }

}
