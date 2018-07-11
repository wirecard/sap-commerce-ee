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

package com.wirecard.hybris.core.operation.impl;

import com.wirecard.hybris.core.data.types.Device;
import com.wirecard.hybris.core.data.types.MerchantAccountId;
import com.wirecard.hybris.core.data.types.ObjectFactory;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.persistence.security.PasswordEncoder;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RatepayInvoiceFingerPrintPaymentProcessorTest {

    @InjectMocks
    private RatepayInvoiceFingerPrintPaymentProcessor ratepayInvoiceFingerPrintPaymentProcessor;

    @Mock
    private PaymentOperationData data;

    @Mock
    private ObjectFactory objectFactory;

    @Mock
    private PasswordEncoder passwordEncoder;

    private String merchantValue;
    private String encoded;


    @Before
    public void setup() {
        merchantValue = "12345";
        encoded = "ZXCVBNM";

        Device device = new Device();

        when(objectFactory.createDevice()).thenReturn(device);
        when(passwordEncoder.encode(Mockito.anyObject(),Mockito.anyObject())).thenReturn(encoded);
    }

    @Test
    public void testPaymentOperation() throws WirecardPaymenException {
        Payment payment = new Payment();

        MerchantAccountId merchantAccountId = new MerchantAccountId();
        merchantAccountId.setValue(merchantValue);
        payment.setMerchantAccountId(merchantAccountId);

        ratepayInvoiceFingerPrintPaymentProcessor.processPayment(payment, data);

        assertEquals("Fingerprint does not match", encoded, payment.getDevice().getFingerprint());
    }
}
