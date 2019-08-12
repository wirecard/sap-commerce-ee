package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.util.PropertyHelper;

public class PayPalMethod extends Payment {

    public static final Payment DEFAULT = new PayPalMethod(
            PaymentType.PAYPAL,
            PropertyHelper.loadProperties().getProperty("paypal.default.email"),
            PropertyHelper.loadProperties().getProperty("paypal.default.password")
    );

    private String username;
    private String password;

    public PayPalMethod() {
        super(PaymentType.PAYPAL, true);
    }

    public PayPalMethod(PaymentType paymentType, String username, String password) {
        super(paymentType, true);
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
