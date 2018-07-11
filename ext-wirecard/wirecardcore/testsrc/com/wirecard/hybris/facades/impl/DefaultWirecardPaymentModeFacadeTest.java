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

package com.wirecard.hybris.facades.impl;

import com.wirecard.hybris.core.converter.data.PaymentModeData;
import com.wirecard.hybris.core.data.SepaMandateData;
import com.wirecard.hybris.core.data.WirecardRequestData;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.strategy.WirecardSignatureStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
public class DefaultWirecardPaymentModeFacadeTest {

    private static final String GUID = "Guid";
    private static final String ISOCODE = "isocode";
    private static final String MAID = "Maid";
    private static final String SIGNATURE = "SignatureV1";
    private static final String PAYMENT_METHOD_CODE = "wd-paypal";

    private DefaultWirecardPaymentModeFacade wirecardPaymentModeFacade;
    @Mock
    private WirecardSignatureStrategy wirecardSignatureStrategy;
    @Mock
    private Converter<PaymentModeModel, PaymentModeData> paymentModeConverter;
    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    @Mock
    private I18NService i18NService;
    @Mock
    private AbstractOrderModel abstractOrderModel;
    @Mock
    private WirecardRequestData wirecardRequestData;
    private PaymentModeModel paymentModeModel;
    private List<PaymentModeModel> paymentModeModelList;
    private PaymentModeData paymentModeData;
    private List<PaymentModeData> paymentModeDataList;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        wirecardPaymentModeFacade = new DefaultWirecardPaymentModeFacade();
        wirecardPaymentModeFacade.setPaymentModeConverter(paymentModeConverter);
        wirecardPaymentModeFacade.setWirecardPaymentConfigurationService(wirecardPaymentConfigurationService);
        wirecardPaymentModeFacade.setI18NService(i18NService);
        wirecardPaymentModeFacade.setWirecardSignatureStrategy(wirecardSignatureStrategy);

        paymentModeModel = new PaymentModeModel();
        paymentModeModel.setActive(true);
        paymentModeModel.setCode(PAYMENT_METHOD_CODE);
        paymentModeModel.setDescription("paypal", Locale.ENGLISH);
        paymentModeModel.setName("paypal", Locale.ENGLISH);
        paymentModeModel.setPaymentAlias("paypal");

        paymentModeData = new PaymentModeData();
        paymentModeData.setCode(PAYMENT_METHOD_CODE);

        paymentModeModelList = Collections.singletonList(paymentModeModel);
        paymentModeDataList = Collections.singletonList(paymentModeData);

        when(paymentModeConverter.convertAll(paymentModeModelList)).thenReturn(paymentModeDataList);
        when(wirecardPaymentConfigurationService.getAllAllowedPaymentModes()).thenReturn(paymentModeModelList);

    }

    @Test
    public void testGetActivePaymentModes() {

        assertFalse("Payment modes list should not be empty ", wirecardPaymentModeFacade.getActivePaymentModes().isEmpty());

    }

    @Test
    public void getSeamlessFormTest() {

        CurrencyModel currency = new CurrencyModel();
        currency.setIsocode(ISOCODE);

        Locale locale = Locale.ENGLISH;

        when(abstractOrderModel.getGuid()).thenReturn(GUID);
        when(abstractOrderModel.getTotalPrice()).thenReturn(2d);
        when(abstractOrderModel.getCurrency()).thenReturn(currency);
        when(i18NService.getCurrentLocale()).thenReturn(locale);

        when(wirecardSignatureStrategy.getMaid(paymentModeModel)).thenReturn(MAID);
        when(wirecardSignatureStrategy.generateSignatureV1(wirecardRequestData, paymentModeModel)).thenReturn(SIGNATURE);

        WirecardRequestData wirecardRequestData =
            wirecardPaymentModeFacade.getSeamlessFormData(abstractOrderModel,
                                                          TransactionType.AUTHORIZATION,
                                                          PaymentMethodName.CREDITCARD,
                                                          paymentModeModel);

        assertEquals("transactionType does not match", TransactionType.AUTHORIZATION.value(),
                     wirecardRequestData.getTransactionType());
        assertEquals("requestedAmount does not match",
                     String.valueOf(abstractOrderModel.getTotalPrice()),
                     wirecardRequestData.getRequestedAmount());
        assertEquals("requestedAmountCurrency does not match",
                     abstractOrderModel.getCurrency().getIsocode(),
                     wirecardRequestData.getRequestedAmountCurrency());
        assertEquals("paymentMethod does not match", PaymentMethodName.CREDITCARD.value(), wirecardRequestData.getPaymentMethod());

    }

    @Test
    public void getSepaMandateDataTest() {
        String creditorID = "12345";
        String creditorName =  "creditor";
        String storeCity = "city";

        WirecardAuthenticationModel authentication = new WirecardAuthenticationModel();
        authentication.setCreditorId(creditorID);
        authentication.setCreditorName(creditorName);
        authentication.setStoreCity(storeCity);
        WirecardPaymentConfigurationModel configuration = new WirecardPaymentConfigurationModel();
        configuration.setAuthentication(authentication);

        when(wirecardPaymentConfigurationService.getConfiguration(Mockito.anyObject())).thenReturn(configuration);

        SepaMandateData sepaMandateData = wirecardPaymentModeFacade.getSepaMandateData(PAYMENT_METHOD_CODE);

        assertNotNull("SEPA mandate data is null", sepaMandateData);
        assertEquals("Creditor ID does not match", creditorID, sepaMandateData.getCreditorId());
        assertEquals("Creditor name does not match", creditorName, sepaMandateData.getCreditorName());
        assertEquals("Store city does not match", storeCity, sepaMandateData.getStoreCity());
    }

    @Test
    public void isPaymentMethodActiveTest() {
        String otherPaymentMethodCode = "paymentMethod2";

        assertTrue("No active payment method found", wirecardPaymentModeFacade.isPaymentMethodActive(PAYMENT_METHOD_CODE));

        paymentModeData.setCode(otherPaymentMethodCode);
        assertFalse("Active payment method found", wirecardPaymentModeFacade.isPaymentMethodActive(PAYMENT_METHOD_CODE));
    }

}
