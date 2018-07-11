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


package com.wirecard.hybris.core.payment.response;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.core.data.types.Statuses;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.payment.response.impl.DefaultRefundResponseHandler;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaypalPaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.warehousing.returns.service.RefundAmountCalculationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardRefundResponseHandlerTest {

    @InjectMocks
    private DefaultRefundResponseHandler defaultWirecardRefundResponseHandler;

    @Mock
    private ReturnRequestModel returnRequestModel;
    @Mock
    private OrderModel orderModel;
    @Mock
    private Statuses statuses;
    @Mock
    private Status status;
    @Mock
    private Payment payment;
    @InjectMocks
    private PaymentOperationData data;
    @Mock
    private ModelService modelService;
    @Mock
    private RefundAmountCalculationService refundAmountCalculationService;
    @Mock
    private WirecardTransactionService wirecardTransactionService;

    @Mock
    private UserModel userModel;
    @Mock
    private PaymentTransactionModel transaction;
    @Mock
    private PaypalPaymentInfoModel paypalPaymentInfoModel;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModel;

    private ReturnRequestModel item;
    private BigDecimal customRefund;
    private BigDecimal originalRefund;

    @Before
    public void setup() {
        item = new ReturnRequestModel();
        customRefund = new BigDecimal("8.00");
        originalRefund = new BigDecimal("10.00");
        when(refundAmountCalculationService.getOriginalRefundAmount(item)).thenReturn(originalRefund);

        when(returnRequestModel.getOrder())
            .thenReturn(orderModel);

        when(orderModel.getUser())
            .thenReturn(userModel);

        when(orderModel.getDeliveryCost())
            .thenReturn(2D);

        when(userModel.getUid())
            .thenReturn("test");

        when(modelService.create(PaymentTransactionModel.class))
            .thenReturn(transaction);

        when(modelService.create(PaymentTransactionEntryModel.class))
            .thenReturn(paymentTransactionEntryModel);

        when(modelService.create(PaypalPaymentInfoModel.class))
            .thenReturn(paypalPaymentInfoModel);

        when(orderModel.getPaymentInfo())
            .thenReturn(paypalPaymentInfoModel);

        when(payment.getRequestId())
            .thenReturn("testId");
        when(payment.getTransactionState())
            .thenReturn(TransactionState.SUCCESS);

        when(payment.getStatuses())
            .thenReturn(statuses);

        when(payment.getTransactionType())
            .thenReturn(TransactionType.AUTHORIZATION);
        when(statuses.getStatus())
            .thenReturn(Collections.singletonList(status));
        when(status.getDescription())
            .thenReturn("Description");
    }


    @Test
    public void executePaymentOperationTest() throws WirecardPaymenException {
        when(this.wirecardTransactionService.getTransactionStatus(payment))
            .thenReturn(TransactionStatus.ACCEPTED);
        defaultWirecardRefundResponseHandler.processResponse( returnRequestModel, data);

        assertEquals("The payment is not correct", returnRequestModel.getOrder().getPaymentInfo(), this.paypalPaymentInfoModel);

    }

    @Test
    public void getOrderTest() {
        AbstractOrderModel order = defaultWirecardRefundResponseHandler.getOrder(returnRequestModel);

        assertEquals("The order does not match", orderModel, order);
    }

    @Test
    public void getOriginalTransactionAmountTest() {
        when(refundAmountCalculationService.getCustomRefundAmount(item)).thenReturn(null);

        BigDecimal transactionAmount = defaultWirecardRefundResponseHandler.getTransactionAmount(item);

        assertEquals("The order does not match", originalRefund, transactionAmount);
    }

    @Test
    public void getCustomTransactionAmountTest() {
        when(refundAmountCalculationService.getCustomRefundAmount(item)).thenReturn(customRefund);

        BigDecimal transactionAmount = defaultWirecardRefundResponseHandler.getTransactionAmount(item);

        assertEquals("The order does not match", customRefund, transactionAmount);
    }
}
