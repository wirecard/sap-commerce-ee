package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.util.PropertyHelper;

public class AlipayMethod extends Payment {

     public static final Payment DEFAULT = new AlipayMethod(
            PropertyHelper.loadProperties().getProperty("alipay.default.email"),
            PropertyHelper.loadProperties().getProperty("alipay.default.password"),
             PropertyHelper.loadProperties().getProperty("alipay.payment.password"),
             PropertyHelper.loadProperties().getProperty("alipay.gaptcha.code")
    );

    private String username;
    private String password;
    private String paypw;
    private String captcha;

    public AlipayMethod() {
        super(PaymentType.PAYPAL, true);
    }

    public AlipayMethod(String username, String password, String paypw, String captcha) {
        super(PaymentType.ALIPAYCB, false);
        this.username = username;
        this.password = password;
        this.paypw = paypw;
        this.captcha = captcha;
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

    public String getPaypw() {
        return paypw;
    }

    public String getCaptcha() {
        return captcha;
    }
}
