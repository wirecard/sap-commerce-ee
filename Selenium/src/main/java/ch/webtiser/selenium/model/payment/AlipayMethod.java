package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.model.CardType;
import ch.webtiser.selenium.util.PropertyHelper;

public class AlipayMethod extends Payment {

     public static final Payment DEFAULT = new AlipayMethod(
            PropertyHelper.loadProperties().getProperty("alipay.default.email"),
            PropertyHelper.loadProperties().getProperty("alipay.default.password")
    );

    private String username;
    private String password;

    public AlipayMethod() {
        super(PaymentType.PAYPAL, true);
    }

    public AlipayMethod(String username, String password) {
        super(PaymentType.ALIPAYCB, false);
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
