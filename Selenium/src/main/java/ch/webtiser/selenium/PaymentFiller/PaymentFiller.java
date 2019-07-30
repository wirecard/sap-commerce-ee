package ch.webtiser.selenium.PaymentFiller;

import ch.webtiser.selenium.model.payment.Payment;

public interface PaymentFiller<T extends Payment> {
    public void fill(final T payment);
}
