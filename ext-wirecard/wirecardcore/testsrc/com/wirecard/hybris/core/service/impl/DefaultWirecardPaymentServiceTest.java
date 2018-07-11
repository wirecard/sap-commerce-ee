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

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardPaymentServiceTest {

    @InjectMocks
    private DefaultWirecardPaymentService wirecardPaymentService;

    @Mock
    private PaymentModeModel paymentModeModel;
    @Mock
    private AbstractOrderModel abstractOrderModel;
    @Mock
    private PaymentTransactionModel transaction1;
    @Mock
    private PaymentTransactionModel transaction2;
    @Mock
    private PaymentTransactionEntryModel transactionEntry1;
    @Mock
    private PaymentTransactionEntryModel transactionEntry2;
    @Mock
    private Payment authorization;
    @Mock
    private Payment authorizationRejected;
    @Mock
    private Payment capture;
    @Mock
    private Payment captureRejected;
    @Mock
    private Payment returnSuccess;
    @Mock
    private Payment returnFail;

    @Mock
    private ReturnRequestModel returnRequestModel;
    @Mock
    private ModelService modelService;
    @Mock
    private WirecardTransactionService wirecardTransactionService;

    private String provider1;
    private String provider2;
    private String subscriptionID;

    @Before
    public void setup() {

        provider1 = "TestPaymentProvider";
        provider2 = "TestPaymentProvider2";
        subscriptionID = "TR01";

        when(abstractOrderModel.getPaymentMode())
            .thenReturn(paymentModeModel);

        when(transaction1.getPaymentProvider()).thenReturn(provider1);
        when(transaction1.getEntries()).thenReturn(Collections.singletonList(transactionEntry1));

        when(transaction2.getPaymentProvider()).thenReturn(provider2);
        when(transaction2.getEntries()).thenReturn(Collections.singletonList(transactionEntry2));

        when(transactionEntry1.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(transactionEntry1.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
        when(transactionEntry1.getSubscriptionID()).thenReturn(subscriptionID);

        when(transactionEntry2.getType()).thenReturn(PaymentTransactionType.CAPTURE);
        when(transactionEntry2.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
        when(transactionEntry2.getSubscriptionID()).thenReturn(subscriptionID);

        when(authorization.getTransactionType())
            .thenReturn(TransactionType.AUTHORIZATION);
        when(authorization.getTransactionState())
            .thenReturn(TransactionState.SUCCESS);

        when(authorizationRejected.getTransactionType())
            .thenReturn(TransactionType.AUTHORIZATION);
        when(authorizationRejected.getTransactionState())
            .thenReturn(TransactionState.FAILED);

        when(capture.getTransactionType())
            .thenReturn(TransactionType.CAPTURE_AUTHORIZATION);
        when(capture.getTransactionState())
            .thenReturn(TransactionState.SUCCESS);
        when(captureRejected.getTransactionType())
            .thenReturn(TransactionType.CAPTURE_AUTHORIZATION);
        when(captureRejected.getTransactionState())
            .thenReturn(TransactionState.FAILED);

        when(returnSuccess.getTransactionState())
            .thenReturn(TransactionState.SUCCESS);
        when(returnFail.getTransactionState())
            .thenReturn(TransactionState.FAILED);

        modelService.save(returnRequestModel);
    }

    @Test
    public void testInvalidCompatibleTransactionSearch() {
        when(abstractOrderModel.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction2));

        PaymentTransactionModel compatibleTransaction = wirecardTransactionService
            .lookForCompatibleTransactions(abstractOrderModel, provider1);

        assertNull("Should have not found a compatible transaction", compatibleTransaction);
    }

    @Test
    public void getReturnStatusSuccessTest() {

        assertEquals("Payment is reversed",
                     ReturnStatus.PAYMENT_REVERSED, wirecardPaymentService.getReturnStatus(returnSuccess));
    }

    @Test
    public void getReturnStatusFailTest() {

        assertEquals("Payment reversal failed",
                     ReturnStatus.PAYMENT_REVERSAL_FAILED, wirecardPaymentService.getReturnStatus(returnFail));
    }


    @Test
    public void getOrderStatusAuthorizeTest() {

        assertEquals("Order status is authorized",
                     OrderStatus.PAYMENT_AUTHORIZED, wirecardPaymentService.getOrderStatus(authorization));
    }

    @Test
    public void getOrderStatusCaptureTest() {

        assertEquals("Order status is captured",
                     OrderStatus.PAYMENT_CAPTURED, wirecardPaymentService.getOrderStatus(capture));
    }

    @Test
    public void getOrderStatusNotAuthorizeTest() {

        assertEquals("Order status is not authorized",
                     OrderStatus.PAYMENT_NOT_AUTHORIZED, wirecardPaymentService.getOrderStatus(authorizationRejected));
    }

    @Test
    public void getOrderStatusNotCaptureTest() {

        assertEquals("Order status is not captured",
                     OrderStatus.PAYMENT_NOT_CAPTURED, wirecardPaymentService.getOrderStatus(captureRejected));
    }



}
