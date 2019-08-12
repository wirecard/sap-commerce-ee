package ch.webtiser.selenium.PaymentFiller;

import ch.webtiser.selenium.model.payment.IdealMethod;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class IdealPaymentFiller extends AbstractPaymentFiller<IdealMethod> {

    private static final By ELEMENT_BIC = By.id("financialInstitution-IDEAL-DD");

    public IdealPaymentFiller(WebDriver driver, Environment env) {
        super(driver, env);
    }

    @Override
    public void fill(final IdealMethod payment) {
        getBicSelect().selectByValue(payment.getBankName());
    }


    private Select getBicSelect() {
        return new Select( driver.findElement(ELEMENT_BIC));
    }

}
