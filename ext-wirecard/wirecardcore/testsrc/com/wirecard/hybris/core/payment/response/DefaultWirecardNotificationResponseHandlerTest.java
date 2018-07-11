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
import com.wirecard.hybris.core.payment.response.impl.DefaultWirecardNotificationResponseHandler;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(Parameterized.class)
public class DefaultWirecardNotificationResponseHandlerTest {

    private static Collection<Object[]> data;

    @Parameters
    public static Collection<Object[]> data() {
        return data;
    }

    static {
        data = new ArrayList<>();
        // non final states change status
        data.add(new Object[] { TransactionState.FAILED, PaymentStatus.PENDING, PaymentStatus.ERROR });
        data.add(new Object[] { TransactionState.SUCCESS, PaymentStatus.PENDING, PaymentStatus.PAID });
        data.add(new Object[] { TransactionState.FAILED, PaymentStatus.NOTPAID, PaymentStatus.ERROR });
        data.add(new Object[] { TransactionState.SUCCESS, PaymentStatus.NOTPAID, PaymentStatus.PAID });
        // final states don't change status
        data.add(new Object[] { TransactionState.FAILED, PaymentStatus.PAID, PaymentStatus.PAID });
        data.add(new Object[] { TransactionState.SUCCESS, PaymentStatus.ERROR, PaymentStatus.ERROR });
    }

    public DefaultWirecardNotificationResponseHandlerTest(TransactionState paymentTransactionState, PaymentStatus orderPaymentStatus,
            PaymentStatus expectedOrderPaymentStatus) {
        super();
        this.expectedOrderPaymentStatus = expectedOrderPaymentStatus;
        this.orderPaymentStatus = orderPaymentStatus;
        this.paymentTransactionState = paymentTransactionState;
    }

    private DefaultWirecardNotificationResponseHandler responseHandler;

    private AbstractOrderModel abstractOrderModel;

    @Mock
    private Statuses statuses;
    @Mock
    private Status status;
    @Mock
    private Payment payment;
    @Mock
    private ModelService modelService;
    @Mock
    private WirecardTransactionService wirecardTransactionService;
    @Mock
    private UserModel userModel;
    @Mock
    private PaymentTransactionModel transaction;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModel;

    private PaymentStatus expectedOrderPaymentStatus;

    private PaymentStatus orderPaymentStatus;

    private TransactionState paymentTransactionState;

    @Before
    public void setup() throws WirecardPaymenException {
        responseHandler = new DefaultWirecardNotificationResponseHandler<>();
        MockitoAnnotations.initMocks(this);
        responseHandler.setModelService(modelService);

        abstractOrderModel = new CartModel();
        abstractOrderModel.setPaymentStatus(orderPaymentStatus);
        abstractOrderModel.setUser(userModel);

        when(userModel.getUid())
            .thenReturn("test");

        when(modelService.create(PaymentTransactionModel.class))
            .thenReturn(transaction);

        when(modelService.create(PaymentTransactionEntryModel.class))
            .thenReturn(paymentTransactionEntryModel);

        when(payment.getRequestId())
            .thenReturn("testId");
        when(payment.getTransactionState())
                .thenReturn(paymentTransactionState);

        when(payment.getStatuses())
            .thenReturn(statuses);

        when(payment.getTransactionType())
                .thenReturn(TransactionType.CAPTURE_AUTHORIZATION);
        when(statuses.getStatus())
            .thenReturn(Collections.singletonList(status));
        when(status.getDescription())
            .thenReturn("Description");

        when(this.wirecardTransactionService.getTransactionStatus(payment))
            .thenReturn(TransactionStatus.ACCEPTED);
    }

    @Test
    public void executeProcessNotificationTest() {

        PaymentOperationData data = new PaymentOperationData();
        data.setPayment(payment);
        try {
            this.responseHandler.processResponse(abstractOrderModel, data);
        } catch (WirecardPaymenException e) {
            // ignore errors because they are expected
        }

        assertEquals("The payment status is not correct", this.expectedOrderPaymentStatus, abstractOrderModel.getPaymentStatus());

    }

}
