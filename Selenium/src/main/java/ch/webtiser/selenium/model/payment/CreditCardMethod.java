package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.model.CardType;
import ch.webtiser.selenium.util.PropertyHelper;

public class CreditCardMethod extends Payment {

    public static final Payment DEFAULT = new CreditCardMethod(
            CardType.valueOf(PropertyHelper.loadProperties().getProperty("creditcard.default.provider")),
            PropertyHelper.loadProperties().getProperty("creditcard.default.account"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.expirationDate.month"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.expirationDate.year"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.cardHolder"),
            PropertyHelper.loadProperties().getProperty("creditcard.default.securityCode")
    );

    private CardType cardProvider;
    private String accountNumber;
    private String expirationDateMont;
    private String expirationDateYear;
    private String cardHolder;
    private String securityCode;

    public CreditCardMethod() {
        super(PaymentType.CREDITCARD, false);
    }

    public CreditCardMethod(CardType cardProvider, String accountNumber, String expirationDateMont, String expirationDateYear, String cardHolder, String securityCode) {
        super(PaymentType.CREDITCARD, false);
        this.cardProvider = cardProvider;
        this.accountNumber = accountNumber;
        this.expirationDateMont = expirationDateMont;
        this.expirationDateYear = expirationDateYear;
        this.cardHolder = cardHolder;
        this.securityCode = securityCode;
    }

    public CardType getCardProvider() {
        return cardProvider;
    }

    public void setCardProvider(CardType cardProvider) {
        this.cardProvider = cardProvider;
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

}
