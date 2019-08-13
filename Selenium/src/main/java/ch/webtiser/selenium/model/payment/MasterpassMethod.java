package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.util.PropertyHelper;

public class MasterpassMethod extends Payment {

    public static final Payment DEFAULT = new MasterpassMethod(
            PropertyHelper.loadProperties().getProperty("masterpass.default.email"),
            PropertyHelper.loadProperties().getProperty("masterpass.default.password"),
            PropertyHelper.loadProperties().getProperty("masterpass.default.wallet")
    );

    private String email;
    private String password;
    private String walletId;

    public MasterpassMethod() {
        super(PaymentType.MASTERPASS, true);
    }

    public MasterpassMethod(String email, String password, String walletId) {
        super(PaymentType.MASTERPASS, true);
        this.email = email;
        this.password = password;
        this.walletId = walletId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
