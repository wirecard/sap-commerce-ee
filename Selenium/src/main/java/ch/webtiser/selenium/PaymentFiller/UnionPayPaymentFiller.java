package ch.webtiser.selenium.PaymentFiller;

import ch.webtiser.selenium.model.payment.UnionPayMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class UnionPayPaymentFiller extends AbstractPaymentFiller<UnionPayMethod> {

    public UnionPayPaymentFiller(WebDriver driver, Environment env) {
        super(driver, env);
    }

    private static final By ELEMENT_FIRSTNAME = By.id("first_name");
    private static final By ELEMENT_LASTNAME= By.id("last_name");
    private static final By ELEMENT_EXPIRATION_DATE_MONTH = By.id("expiration_month_list");
    private static final By ELEMENT_EXPIRATION_DATE_YEAR = By.id("expiration_year_list");
    private static final By ELEMENT_CARDNUMBER = By.id("account_number");
    private static final By ELEMENT_SECURITY_CODE= By.id("card_security_code");

    @Override
    public void fill(final UnionPayMethod payment) {
        switchToFrame(payment);
        getFirstNameInput().sendKeys(payment.getFirstname());
        getLastNameInput().sendKeys(payment.getLastName());
        getExpirationDateMonthSelect().selectByValue(payment.getExpirationDateMont());
        getExpirationDateYearSelect().selectByValue(payment.getExpirationDateYear());
        getCardNumberInput().sendKeys(payment.getCardNumber());
        getSecurityCodeInput().sendKeys(payment.getSecurityCode());
        driver.switchTo().defaultContent();
    }

    private WebElement getFirstNameInput() {
        return driver.findElement(ELEMENT_FIRSTNAME);
    }

    private WebElement getLastNameInput() {
        return driver.findElement(ELEMENT_LASTNAME);
    }

    private WebElement getCardNumberInput() {
        return driver.findElement(ELEMENT_CARDNUMBER);
    }

    private WebElement getSecurityCodeInput() {
        return driver.findElement(ELEMENT_SECURITY_CODE);
    }

    private Select getExpirationDateMonthSelect() {
        return new Select( driver.findElement(ELEMENT_EXPIRATION_DATE_MONTH));
    }

    private Select getExpirationDateYearSelect() {
        return new Select( driver.findElement(ELEMENT_EXPIRATION_DATE_YEAR));
    }

}
