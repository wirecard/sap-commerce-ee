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

package com.wirecard.hybris.core.operation;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.operation.impl.FallbackPaymentOperation;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.exception.WirecardTriggerOperationFallbackException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FallbackPaymentOperationTest {

    private FallbackPaymentOperation<AbstractOrderModel> fallbackPaymentOperation;

    @Mock
    private PaymentOperation<AbstractOrderModel> decisionOperation;
    @Mock
    private PaymentOperation<AbstractOrderModel> successOperation;
    @Mock
    private PaymentOperation<AbstractOrderModel> errorOperation;

    private AbstractOrderModel order;
    private PaymentOperationData paymentOperationData;
    private Payment decisionPaymentData;
    private Payment successPaymentData;
    private Payment errorPaymentData;
    private WirecardTriggerOperationFallbackException decisionOperationException;

    @Before
    public void setup() throws WirecardPaymenException {
        fallbackPaymentOperation = new FallbackPaymentOperation<>();
        fallbackPaymentOperation.setDecisionOperation(decisionOperation);

        order = new OrderModel();
        paymentOperationData = new PaymentOperationData();
        decisionPaymentData = new Payment();
        successPaymentData = new Payment();
        errorPaymentData = new Payment();
        decisionOperationException = new WirecardTriggerOperationFallbackException("Decision operation failed");

        when(decisionOperation.doOperation(order, paymentOperationData)).thenReturn(decisionPaymentData);
        when(successOperation.doOperation(order, paymentOperationData)).thenReturn(successPaymentData);
        when(errorOperation.doOperation(order, paymentOperationData)).thenReturn(errorPaymentData);
    }


    @Test
    public void testDefaultOperation() throws WirecardPaymenException {
        fallbackPaymentOperation.setSuccessOperation(null);
        fallbackPaymentOperation.setErrorOperation(null);

        Payment payment = fallbackPaymentOperation.doOperation(order, paymentOperationData);

        assertEquals("Payment data does not match", decisionPaymentData, payment);
    }

    @Test
    public void testSuccessOperation() throws WirecardPaymenException {
        fallbackPaymentOperation.setSuccessOperation(successOperation);
        fallbackPaymentOperation.setErrorOperation(errorOperation);

        Payment payment = fallbackPaymentOperation.doOperation(order, paymentOperationData);

        assertEquals("Payment data does not match", successPaymentData, payment);
    }

    @Test
    public void testErrorOperation() throws WirecardPaymenException {
        fallbackPaymentOperation.setSuccessOperation(null);
        fallbackPaymentOperation.setErrorOperation(errorOperation);

        when(decisionOperation.doOperation(order, paymentOperationData)).thenThrow(decisionOperationException);

        Payment payment = fallbackPaymentOperation.doOperation(order, paymentOperationData);

        assertEquals("Payment data does not match", errorPaymentData, payment);
    }

    @Test(expected = WirecardPaymenException.class)
    public void testException() throws WirecardPaymenException {
        fallbackPaymentOperation.setSuccessOperation(null);
        fallbackPaymentOperation.setErrorOperation(null);

        when(decisionOperation.doOperation(order, paymentOperationData)).thenThrow(decisionOperationException);

        fallbackPaymentOperation.doOperation(order, paymentOperationData);
    }
}
