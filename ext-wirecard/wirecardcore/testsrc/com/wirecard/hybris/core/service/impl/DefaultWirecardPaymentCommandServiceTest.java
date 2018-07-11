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

package com.wirecard.hybris.core.service.impl;

import com.wirecard.hybris.core.converter.xml.PaymentConverter;
import com.wirecard.hybris.core.data.types.Device;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethod;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.PaymentMethods;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.exception.WirecardInvalidSignatureException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardPaymentCommandServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWirecardPaymentCommandService.class);

    private static final String RESPONSE_NULL = "Response is null, there is an error in server";
    private static final String RESPONSE_EQUALS = "Response does not match";
    private static final String PLACEHOLDER_REQUEST_ID = "${requestId}";
    private static final String AUTHORIZATION_URL = "https://api-test.wirecard.com/engine/rest/paymentmethods/";
    private static final String CAPTURE_URL = "https://api-test.wirecard.com/engine/rest/payments/";
    private static final String REFUND_URL = "https://api-test.wirecard.com/engine/rest/payments/";
    public static final String FINGERPRINT = "fingerprint";

    @InjectMocks
    private DefaultWirecardPaymentCommandService defaultWirecardPayPalService;

    @Mock
    private PaymentConverter paymentConverter;
    @Mock
    private BasicResponseHandler basicResponseHandler;
    @Mock
    private Payment payment;
    @Mock
    private WirecardAuthenticationModel wirecardPaymentMethodConfiguration;
    private String xml;
    @Mock
    PaymentMethod paymentMethod;
    @Mock
    PaymentMethods paymentMethods;
    @Mock
    List<PaymentMethod> paymentMethodList;
    @Mock
    Device device;

    @Before
    public void setup() throws IOException, WirecardInvalidSignatureException, WirecardPaymenException {

        xml = "<xml/>";

        paymentMethodList = Collections.singletonList(paymentMethod);

        when(basicResponseHandler.handleResponse(Mockito.anyObject()))
            .thenReturn(xml);

        when(wirecardPaymentMethodConfiguration.getUsername())
            .thenReturn("username");

        when(wirecardPaymentMethodConfiguration.getUsername())
            .thenReturn("username");

        when(wirecardPaymentMethodConfiguration.getPassword())
            .thenReturn("password");

        when(paymentConverter.convertDataToXML(payment))
            .thenReturn(xml);

        when(paymentConverter.convertXMLToData(xml))
            .thenReturn(payment);

        when(payment.getPaymentMethods()).thenReturn(paymentMethods);

        when(paymentMethods.getPaymentMethod()).thenReturn(paymentMethodList);

        when(payment.getDevice()).thenReturn(device);

    }

    @Test
    public void testAuthorizationSendRequest() throws WirecardPaymenException {

        when(paymentMethod.getName()).thenReturn(PaymentMethodName.PAYPAL);

        Payment response =
            defaultWirecardPayPalService.sendRequest(payment, AUTHORIZATION_URL, wirecardPaymentMethodConfiguration);

        assertNotNull(RESPONSE_NULL, response);
        assertEquals(RESPONSE_EQUALS, payment, response);

    }

    @Test
    public void testCaptureSendRequest() throws WirecardPaymenException {

        when(paymentMethod.getName()).thenReturn(PaymentMethodName.PAYPAL);

        Payment response =
            defaultWirecardPayPalService.sendRequest(payment, CAPTURE_URL, wirecardPaymentMethodConfiguration);

        assertNotNull(RESPONSE_NULL, response);
        assertEquals(RESPONSE_EQUALS, this.payment, response);

    }

    @Test
    public void refundTest() throws WirecardPaymenException {

        when(paymentMethod.getName()).thenReturn(PaymentMethodName.PAYPAL);

        Payment response =
            defaultWirecardPayPalService.sendRequest(payment, REFUND_URL, wirecardPaymentMethodConfiguration);

        assertNotNull(RESPONSE_NULL, response);
        assertEquals(RESPONSE_EQUALS, this.payment, response);

    }

    @Test
    public void fingerPrintTest() throws WirecardPaymenException {

        when(paymentMethod.getName()).thenReturn(PaymentMethodName.RATEPAY_INVOICE);

        when(device.getFingerprint()).thenReturn(FINGERPRINT);

        Payment response =
            defaultWirecardPayPalService.sendRequest(payment, REFUND_URL, wirecardPaymentMethodConfiguration);

        when(response.getDevice()).thenReturn(device);

        assertNotNull(RESPONSE_NULL, response);
        assertEquals(RESPONSE_EQUALS, this.payment, response);

    }

}
