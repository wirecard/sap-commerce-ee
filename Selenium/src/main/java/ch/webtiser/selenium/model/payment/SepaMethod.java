package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.util.PropertyHelper;

public class SepaMethod extends Payment {

    public static final Payment DEFAULT = new SepaMethod(
            PropertyHelper.loadProperties().getProperty("sepa.default.accountHolderName"),
            PropertyHelper.loadProperties().getProperty("sepa.default.iban")
    );


    private String accountHolderName;
    private String iban;

    public SepaMethod(String accountHolderName, String iban) {
        super(PaymentType.SEPA, true);
        this.accountHolderName = accountHolderName;
        this.iban = iban;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}
