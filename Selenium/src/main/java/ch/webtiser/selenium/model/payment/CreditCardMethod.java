package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.util.PropertyHelper;

public class CreditCardMethod extends Payment {

    public static final Payment DEFAULT = new CreditCardMethod(
            PropertyHelper.loadProperties().getProperty("creditcard.default.account"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.expirationDate.month"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.expirationDate.year"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.cardHolder"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.securityCode"),
           false,
            ""
    );

    public static final Payment DEFAULT_3D = new CreditCardMethod(
            PropertyHelper.loadProperties().getProperty("creditcard.default3D.account"),
            PropertyHelper.loadProperties().getProperty("creditcard.default3D.expirationDate.month"),
            PropertyHelper.loadProperties().getProperty("creditcard.default3D.expirationDate.year"),
            PropertyHelper.loadProperties().getProperty("creditcard.default3D.cardHolder"),
            PropertyHelper.loadProperties().getProperty("creditcard.default3D.securityCode"),
            true,
            PropertyHelper.loadProperties().getProperty("creditcard.default3D.password3D")

    );

    private String accountNumber;
    private String expirationDateMont;
    private String expirationDateYear;
    private String cardHolder;
    private String securityCode;
    private Boolean is3DSecured;

    private String password3D;

    public CreditCardMethod() {
        super(PaymentType.CREDITCARD, false);
    }

    public CreditCardMethod(String accountNumber, String expirationDateMont, String expirationDateYear, String cardHolder, String securityCode, Boolean is3DSecured, String password3D) {
        super(PaymentType.CREDITCARD, is3DSecured);
        this.accountNumber = accountNumber;
        this.expirationDateMont = expirationDateMont;
        this.expirationDateYear = expirationDateYear;
        this.cardHolder = cardHolder;
        this.securityCode = securityCode;
        this.is3DSecured = is3DSecured;
        this.password3D = password3D;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getExpirationDateMont() {
        return expirationDateMont;
    }

    public void setExpirationDateMont(String expirationDateMont) {
        this.expirationDateMont = expirationDateMont;
    }

    public String getExpirationDateYear() {
        return expirationDateYear;
    }

    public void setExpirationDateYear(String expirationDateYear) {
        this.expirationDateYear = expirationDateYear;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public Boolean getIs3DSecured() {
        return is3DSecured;
    }

    public void setIs3DSecured(Boolean is3DSecured) {
        this.is3DSecured = is3DSecured;
    }

    public String getPassword3D() {
        return password3D;
    }

    public void setPassword3D(String password3D) {
        this.password3D = password3D;
    }
}
