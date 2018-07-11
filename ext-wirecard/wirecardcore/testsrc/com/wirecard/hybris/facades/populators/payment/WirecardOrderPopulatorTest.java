/*
 * Shop System Plugins - Terms of Use
 *
 * The plugins offered are provided free of charge by Wirecard AG and are explicitly not part
 * of the Wirecard AG range of products and services.
 *
 * They have been tested and approved for full functionality in the standard configuration
 * (status on delivery) of the corresponding shop system. They are under MIT license
 * and can be used, developed and passed on to third parties under
 * the same terms.
 *
 * However, Wirecard AG does not provide any guarantee or accept any liability for any errors
 * occurring when used in an enhanced, customized shop system configuration.
 *
 * Operation in an enhanced, customized configuration is at your own risk and requires a
 * comprehensive test phase by the user of the plugin.
 *
 * Customers use the plugins at their own risk. Wirecard AG does not guarantee their full
 * functionality neither does Wirecard AG assume liability for any disadvantages related to
 * the use of the plugins. Additionally, Wirecard AG does not guarantee the full functionality
 * for customized shop systems or installed plugins of other vendors of plugins within the same
 * shop system.
 *
 * Customers are responsible for testing the plugin's functionality before starting productive
 * operation.
 *
 * By installing the plugin into the shop system the customer agrees to these terms of use.
 * Please do not use the plugin if you do not agree to these terms of use!
 */

package com.wirecard.hybris.facades.populators.payment;

import com.wirecard.hybris.core.converter.data.WirecardPaymentInfoData;
import com.wirecard.hybris.facades.populators.WirecardOrderPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@UnitTest
public class WirecardOrderPopulatorTest extends WirecardOrderPopulator{

    @Mock
    private AbstractOrderModel source;
    @Mock
    private Converter<PaymentInfoModel, WirecardPaymentInfoData> wirecardPaymentInfoConverter;

    private PaymentInfoModel paymentInfoModel;
    private String firstName;
    private WirecardPaymentInfoData target;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        firstName = "FIRSTNAME";

        paymentInfoModel = new PaymentInfoModel();
        paymentInfoModel.setBillingAddress(new AddressModel());
        paymentInfoModel.getBillingAddress().setFirstname(firstName);

        when(source.getPaymentInfo()).thenReturn(paymentInfoModel);

        when(wirecardPaymentInfoConverter.convert(paymentInfoModel)).thenReturn(target);

    }

    @Test
    public void addPaymentInformationTest() {

        AbstractOrderData prototype = new AbstractOrderData();
        this.addPaymentInformation(source, prototype);

        //compare both datas
        assertEquals("PaymentInfo does not match", target,
                     prototype.getPaymentInfo());

    }

    @Test
    public void populateTest() {

        AbstractOrderData prototype = new AbstractOrderData();
        this.addPaymentInformation(source, prototype);

        //compare both datas
        assertEquals("PaymentInfo does not match", target,
                     prototype.getPaymentInfo());

    }

}
