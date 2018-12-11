package ch.webtiser.selenium.model.payment;

import ch.webtiser.selenium.model.Country;
import ch.webtiser.selenium.util.PropertyHelper;

public class PoiPaiMethod extends Payment {

     public static final Payment DEFAULT = new PoiPaiMethod();

    public PoiPaiMethod() {
        super(PaymentType.POI_PAI, false);
    }
}
