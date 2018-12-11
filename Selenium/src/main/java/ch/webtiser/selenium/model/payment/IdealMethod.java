package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.model.Country;
import ch.webtiser.selenium.util.PropertyHelper;

public class IdealMethod extends Payment {

    public static final Payment DEFAULT = new IdealMethod(
            PropertyHelper.loadProperties().getProperty("ideal.default.bankName")
    );


    private String bankName;

    public IdealMethod(String bankName) {
        super(PaymentType.IDEAL, false);
        this.bankName = bankName;
    }

    public static Payment getDEFAULT() {
        return DEFAULT;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
