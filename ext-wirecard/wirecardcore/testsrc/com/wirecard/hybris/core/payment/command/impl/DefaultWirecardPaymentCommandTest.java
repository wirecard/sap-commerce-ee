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

package com.wirecard.hybris.core.payment.command.impl;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.service.PaymentCommandService;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardPaymentCommandTest {

    @InjectMocks
    private DefaultWirecardPaymentCommand defaultWirecardPaymentCommand;

    @Mock
    private PaymentCommandService paymentCommandService;
    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    @Mock
    private Payment payment;
    @Mock
    private WirecardAuthenticationModel wirecardAuthentication;

    private static final String REQUESTURL = "wirecard.url.capture";
    private static final String CODE = "PAYPAL";

    @Before
    public void setup() throws WirecardPaymenException {
        String DEFAULT_URL = "https://api-test.wirecard.com";
        String CAPTURE_URL = "/engine/rest/payments/";
        String URL = "https://api-test.wirecard.com/engine/rest/payments/";

        wirecardAuthentication = new WirecardAuthenticationModel();
        wirecardAuthentication.setBaseUrl(DEFAULT_URL);
        defaultWirecardPaymentCommand.setRequestUrl(REQUESTURL);

        when(wirecardPaymentConfigurationService.getWirecardParameter(REQUESTURL)).thenReturn(CAPTURE_URL);
        when(paymentCommandService.sendRequest(payment, URL, wirecardAuthentication)).thenReturn(payment);
    }

    @Test
    public void executePaymentOperationTest() throws WirecardPaymenException {
        Payment payment = defaultWirecardPaymentCommand.execute(this.payment, wirecardAuthentication);
        assertEquals("The capture did not execute properly", this.payment, payment);
    }
}
