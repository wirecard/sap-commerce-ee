package ch.webtiser.selenium.model.payment;


public enum PaymentType {
    CREDITCARD("creditcard"), PAYPAL("paypal"), SEPA("sepadirectdebit"), SOFORT("sofortbanking"), IDEAL("ideal"), GUARANTEED_INVOICE("wiretransfer"), POI_PAI("wiretransfer"), MASTERPASS("masterpass"), ALIPAYCB("alipay_xborder"), UNIONPAY("unionpay");

    public static final PaymentType DEFAULT = PAYPAL;

    private String code;

    PaymentType(final String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.name().toUpperCase();
    }

    public String getCode() {
        return code;
    }
}
