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

import com.wirecard.hybris.core.data.types.BankAccount;
import com.wirecard.hybris.core.data.types.ObjectFactory;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class IdealDebitBicPaymentProcessorTest {

    @InjectMocks
    private IdealDebitBicPaymentProcessor idealDebitBicPaymentProcessor;

    @Mock
    private PaymentOperationData data;

    @Mock
    private ObjectFactory objectFactory;

    private String iban;
    private String bic;
    private String redirectUrl;
    private String requestId;
    private String expected;


    @Before
    public void setup() {
        iban = "AA1234567890";
        bic = "ABCDEF";
        redirectUrl = "start";
        requestId = "12345";
        expected = redirectUrl + "?status=success&request_id=" + requestId;

        BankAccount bankAccount = new BankAccount();

        when(objectFactory.createBankAccount()).thenReturn(bankAccount);
        when(data.getIban()).thenReturn(iban);
        when(data.getBic()).thenReturn(bic);
    }

    @Test
    public void testPaymentOperation() throws WirecardPaymenException {
        Payment payment = new Payment();

        payment.setSuccessRedirectUrl(redirectUrl);
        payment.setRequestId(requestId);

        idealDebitBicPaymentProcessor.processPayment(payment, data);

        assertEquals("Iban does not match", iban, payment.getBankAccount().getIban());
        assertEquals("Bic does not match", bic, payment.getBankAccount().getBic());
        assertEquals("SuccessUrl does not match", expected, payment.getSuccessRedirectUrl());
    }
}
