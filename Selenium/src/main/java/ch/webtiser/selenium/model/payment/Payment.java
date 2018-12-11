package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.model.CardType;
import ch.webtiser.selenium.util.PropertyHelper;

public class Payment {
    private PaymentType paymentType = null;
    private boolean hasAuthentificatio;

    public static final Payment DEFAULT = IdealMethod.DEFAULT;


    public Payment(final PaymentType paymentType, final boolean hasAuthentificatio) {
        this.paymentType = paymentType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public boolean isHasAuthentificatio() {
        return hasAuthentificatio;
    }

    public void setHasAuthentificatio(boolean hasAuthentificatio) {
        this.hasAuthentificatio = hasAuthentificatio;
    }
}
