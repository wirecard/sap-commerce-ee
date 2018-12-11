package ch.webtiser.selenium.PaymentFiller;

import ch.webtiser.selenium.model.payment.SepaMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SepaPaymentFiller extends AbstractPaymentFiller<SepaMethod> {

    public SepaPaymentFiller(WebDriver driver, Environment env) {
        super(driver, env);
    }


    private static final By ELEMENT_ACCOUNTOWNER = By.id("bankAccountOwner-SEPA-DD");
    private static final By ELEMENT_BIC = By.id("bankAccountIban-SEPA-DD");

    @Override
    public void fill(final SepaMethod payment) {
        getAccountOwnerInput().sendKeys(payment.getAccountHolderName());
        getIbanInput().sendKeys(payment.getIban());
    }


    private WebElement getAccountOwnerInput() {
        return driver.findElement(ELEMENT_ACCOUNTOWNER);
    }

    private WebElement getIbanInput() {
        return driver.findElement(ELEMENT_BIC);
    }

}
