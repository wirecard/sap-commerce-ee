package ch.webtiser.selenium.PaymentFiller;

import ch.webtiser.selenium.model.payment.Payment;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.WebDriver;

public class DefaultPaymentFiller extends AbstractPaymentFiller {

    public DefaultPaymentFiller(WebDriver driver, Environment env) {
        super(driver, env);
    }

    @Override
    public void fill(final Payment payment) {
    }
}
