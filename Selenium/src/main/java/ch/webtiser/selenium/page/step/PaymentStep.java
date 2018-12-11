package ch.webtiser.selenium.page.step;

import ch.webtiser.selenium.PaymentFiller.*;
import ch.webtiser.selenium.model.payment.Payment;
import ch.webtiser.selenium.model.payment.PaymentType;
import ch.webtiser.selenium.util.enums.Environment;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PaymentStep extends AbstractStep<Payment> {
    private static final String PAYMENT_TYPE_PREFIX= "wd-";

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

    public void setPaymentType(final PaymentType paymentType) {
        getPaymentTypeRadio(paymentType.getCode().toString()).click();
    }

    private WebElement getPaymentTypeRadio(final String type) {
        return getElementById(By.id(PAYMENT_TYPE_PREFIX + type.toLowerCase()));
    }



}
