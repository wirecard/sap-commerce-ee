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
import com.wirecard.hybris.core.operation.PaymentOperation;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.strategy.PaymentOperationStrategy;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardPOExecutionServiceTest {

    @InjectMocks
    private DefaultWirecardPOExecutionService wirecardPaymentOperationExecutionService;

    @Mock
    private DefaultWirecardPaymentService wirecardPaymentService;
    @Mock
    private DefaultWirecardTransactionService wirecardTransactionService;
    @Mock
    private PaymentOperationStrategy paymentOperationStrategy;

    @Mock
    private AbstractOrderModel abstractOrderModel;
    @Mock
    private OrderModel orderModel;
    @Mock
    private PaymentTransactionModel transaction1;
    @Mock
    private PaymentTransactionModel transaction2;
    @Mock
    private PaymentOperation paymentOperation;
    @Mock
    private Payment capture;
    @Mock
    private ReturnRequestModel returnRequestModel;
    @Mock
    private Payment returnSuccess;
    @Mock
    private PaymentModeModel paymentModeModel;
    @Mock
    private ReturnStatus returnStatus;

    private String provider1;
    private String provider2;
    private PaymentOperationData additionalParameters;

    @Before
    public void setup() {

        provider1 = "TestPaymentProvider";
        provider2 = "TestPaymentProvider2";

        when(abstractOrderModel.getPaymentMode()).thenReturn(paymentModeModel);
        when(orderModel.getPaymentMode()).thenReturn(paymentModeModel);
        when(wirecardTransactionService.lookForAcceptedTransactions(abstractOrderModel, PaymentTransactionType.AUTHORIZATION)).thenReturn(
            true);
    }


    @Test
    public void executePaymentCaptureOperationSucessTest() throws WirecardPaymenException {

        when(abstractOrderModel.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction1));
        when(abstractOrderModel.getPaymentMode()).thenReturn(paymentModeModel);

        when(paymentModeModel.getCode()).thenReturn(provider1);
        when(paymentOperationStrategy.getOperation(abstractOrderModel.getPaymentMode(), PaymentTransactionType.CAPTURE.name()))
            .thenReturn(paymentOperation);
        try {
            when(paymentOperation.doOperation(abstractOrderModel, null))
                .thenReturn(capture);
        } catch (WirecardPaymenException e) {
            e.printStackTrace();

        }
        when(wirecardPaymentService.getOrderStatus(capture)).thenReturn(OrderStatus.PAYMENT_CAPTURED);

        assertEquals(OrderStatus.PAYMENT_CAPTURED,
                     wirecardPaymentOperationExecutionService.executePaymentCaptureOperation(abstractOrderModel, additionalParameters));

    }

    @Test
    public void executePaymentCaptureOperationFailureTest() throws WirecardPaymenException {

        when(abstractOrderModel.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction1));
        when(abstractOrderModel.getPaymentMode()).thenReturn(paymentModeModel);

        when(paymentModeModel.getCode()).thenReturn(provider1);
        when(paymentOperationStrategy.getOperation(abstractOrderModel.getPaymentMode(), PaymentTransactionType.CAPTURE.name()))
            .thenReturn(paymentOperation);
        when(paymentOperation.doOperation(abstractOrderModel, null))
            .thenThrow(WirecardPaymenException.class);

        OrderStatus orderStatus =
            wirecardPaymentOperationExecutionService.executePaymentCaptureOperation(abstractOrderModel, additionalParameters);

        assertEquals("OrderStatus does not match", OrderStatus.PAYMENT_NOT_CAPTURED, orderStatus);

    }

    @Test
    public void executePaymentRefundOperationSuccessTest() throws WirecardPaymenException {

        when(returnRequestModel.getOrder()).thenReturn(orderModel);
        when(wirecardTransactionService.lookForAcceptedTransactions(orderModel, PaymentTransactionType.CAPTURE))
            .thenReturn(true);
        when(orderModel.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction2));
        when(orderModel.getPaymentMode()).thenReturn(paymentModeModel);
        when(paymentModeModel.getCode()).thenReturn(provider2);

        when(paymentOperationStrategy.getOperation(returnRequestModel.getOrder().getPaymentMode(),
                                                   PaymentTransactionType.REFUND_FOLLOW_ON.name()))
            .thenReturn(paymentOperation);

        try {
            when(paymentOperation.doOperation(returnRequestModel, null))
                .thenReturn(returnSuccess);
        } catch (WirecardPaymenException e) {
            e.printStackTrace();

        }
        when(wirecardPaymentService.getReturnStatus(returnSuccess)).thenReturn(ReturnStatus.PAYMENT_REVERSED);

        ReturnStatus returnStatus = wirecardPaymentOperationExecutionService.executePaymentRefundOperation(returnRequestModel, null);

        assertEquals("ReturnStatus does not match", ReturnStatus.PAYMENT_REVERSED, returnStatus);

    }

    @Test
    public void executePaymentRefundOperationFailureTest() throws WirecardPaymenException {

        when(returnRequestModel.getOrder()).thenReturn(orderModel);
        when(wirecardTransactionService.lookForAcceptedTransactions(orderModel, PaymentTransactionType.CAPTURE)).thenReturn(
            true);
        when(orderModel.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction2));
        when(orderModel.getPaymentMode()).thenReturn(paymentModeModel);
        when(paymentModeModel.getCode()).thenReturn(provider2);

        when(paymentOperationStrategy.getOperation(returnRequestModel.getOrder().getPaymentMode(),
                                                   PaymentTransactionType.REFUND_FOLLOW_ON.name()))
            .thenReturn(paymentOperation);

        when(paymentOperation.doOperation(returnRequestModel, null))
            .thenThrow(WirecardPaymenException.class);
        when(wirecardPaymentService.getReturnStatus(null)).thenReturn(ReturnStatus.PAYMENT_REVERSAL_FAILED);

        ReturnStatus returnStatus = wirecardPaymentOperationExecutionService.executePaymentRefundOperation(returnRequestModel, null);

        assertEquals("ReturnStatus does not match", ReturnStatus.PAYMENT_REVERSAL_FAILED, returnStatus);
    }

}
