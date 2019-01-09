package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.util.PropertyHelper;

public class UnionPayMethod extends Payment {

    public static final Payment DEFAULT = new UnionPayMethod(
            false,
            PropertyHelper.loadProperties().getProperty("unionpay.default.cardNumber"),
            PropertyHelper.loadProperties().getProperty("unionpay.default.securityCode"),
            PropertyHelper.loadProperties().getProperty("unionpay.default.expirationDate.month"),
            PropertyHelper.loadProperties().getProperty("unionpay.default.expirationDate.year"),
            PropertyHelper.loadProperties().getProperty("unionpay.default.firstName"),
            PropertyHelper.loadProperties().getProperty("unionpay.default.lastName")
    );

    public UnionPayMethod(boolean hasAuthentification, String cardNumber, String securityCode, String expirationDateMont, String expirationDateYear, String firstname, String lastName) {
        super(PaymentType.UNIONPAY, hasAuthentification);
        this.cardNumber = cardNumber;
        this.securityCode = securityCode;
        this.expirationDateMont = expirationDateMont;
        this.expirationDateYear = expirationDateYear;
        this.firstname = firstname;
        this.lastName = lastName;
    }

    public UnionPayMethod() {
        super(PaymentType.UNIONPAY, false);
    }

    private String cardNumber;
    private String securityCode;
    private String expirationDateMont;
    private String expirationDateYear;
    private String firstname;
    private String lastName;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
