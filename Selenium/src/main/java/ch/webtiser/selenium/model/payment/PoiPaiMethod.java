package ch.webtiser.selenium.model.payment;

public class PoiPaiMethod extends Payment {

     public static final Payment DEFAULT = new PoiPaiMethod();

    public PoiPaiMethod() {
        super(PaymentType.POI_PAI, false);
    }
}
