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

import com.wirecard.hybris.core.data.types.Card;
import com.wirecard.hybris.core.data.types.CardToken;
import com.wirecard.hybris.core.data.types.CardType;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.core.data.types.Statuses;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.payment.response.impl.WirecardCreditCardAuthorizationResponseHandler;
import com.wirecard.hybris.core.payment.response.impl.WirecardCreditCardCheckEnrollmentResponseHandler;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.exception.WirecardNotEnrolledException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WirecardCreditCardAuthorizationResponseHandlerTest {

    private static final String tokenId = "tokenId";

    @InjectMocks
    private WirecardCreditCardAuthorizationResponseHandler wirecardCreditCardAuthorizationResponseHandler;

    @InjectMocks
    private WirecardCreditCardCheckEnrollmentResponseHandler wirecardCreditCardCheckEnrollmentResponseHandler;


    @Mock
    private AbstractOrderModel abstractOrderModel;
    @Mock
    private Payment payment;
    @Mock
    private Statuses statuses;
    @Mock
    private CardToken cardToken;
    @Mock
    private Card card;
    @Mock
    private Status status;
    @Mock
    private ModelService modelService;
    @Mock
    private FlexibleSearchService flexibleSearchService;
    @Mock
    private WirecardTransactionService wirecardTransactionService;
    @Mock
    private UserModel userModel;
    @Mock
    private PaymentTransactionModel transaction;
    @Mock
    private PaymentModeModel paymentMode;
    @Mock
    private CreditCardPaymentInfoModel creditCardPaymentInfoModel;
    @Mock
    private PaymentTransactionEntryModel paymentTransactionEntryModel;
    @Mock
    private WirecardPaymentService wirecardPaymentService;
    @Mock
    private CustomerModel customerModel;
    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    private Collection<String> checkEnrollmentErrors;

    @Before
    public void setup() {

        String transactionErrorCode = "500";
        checkEnrollmentErrors = Collections.singleton(transactionErrorCode);
        wirecardCreditCardCheckEnrollmentResponseHandler.setCheckEnrollmentErrors(checkEnrollmentErrors);

        when(abstractOrderModel.getUser())
            .thenReturn(userModel);

        when(userModel.getUid())
            .thenReturn("test");

        when(modelService.create(PaymentTransactionModel.class))
            .thenReturn(transaction);

        when(modelService.create(PaymentTransactionEntryModel.class))
            .thenReturn(paymentTransactionEntryModel);

        when(flexibleSearchService.getModelsByExample(Mockito.anyObject()))
            .thenReturn(null);

        when(modelService.create(CreditCardPaymentInfoModel.class))
            .thenReturn(creditCardPaymentInfoModel);

        when(abstractOrderModel.getPaymentInfo())
            .thenReturn(creditCardPaymentInfoModel);

        when(payment.getRequestId())
            .thenReturn("testId");
        when(payment.getTransactionState())
            .thenReturn(TransactionState.SUCCESS);

        when(payment.getStatuses())
            .thenReturn(statuses);

        when(payment.getCardToken())
            .thenReturn(cardToken);
        when(cardToken.getTokenId())
            .thenReturn(tokenId);

        when(payment.getCard())
            .thenReturn(card);
        when(card.getCardType())
            .thenReturn(CardType.MAESTRO);
        when(card.getExpirationMonth())
            .thenReturn((short) 1);
        when(card.getExpirationYear())
            .thenReturn((short) 2019);

        when(payment.getTransactionType())
            .thenReturn(TransactionType.AUTHORIZATION);
        when(statuses.getStatus())
            .thenReturn(Collections.singletonList(status));
        when(status.getDescription())
            .thenReturn("Description");
        when(status.getCode())
            .thenReturn(transactionErrorCode);

        when(abstractOrderModel.getPaymentMode())
            .thenReturn(paymentMode);

        when(paymentMode.getCode())
            .thenReturn("111");

        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customerModel);
    }


    @Test
    public void processResponseTest() throws WirecardPaymenException {

        PaymentOperationData data = new PaymentOperationData();
        data.setPayment(payment);
        wirecardCreditCardAuthorizationResponseHandler.processResponse( abstractOrderModel, data);

        assertEquals("The payment is not correct", this.creditCardPaymentInfoModel, abstractOrderModel.getPaymentInfo());

    }

    @Test
    public void processCheckEnrollmentSuccessfulResponseTest() throws WirecardPaymenException {

        PaymentOperationData data = new PaymentOperationData();
        data.setPayment(payment);
        wirecardCreditCardCheckEnrollmentResponseHandler.processResponse( abstractOrderModel, data);

        assertEquals("The payment is not correct", this.creditCardPaymentInfoModel, abstractOrderModel.getPaymentInfo());

    }

    @Test(expected = WirecardNotEnrolledException.class)
    public void processCheckEnrollmentControlledErrorResponseTest() throws WirecardPaymenException {
        when(payment.getTransactionState()).thenReturn(TransactionState.FAILED);
        PaymentOperationData data = new PaymentOperationData();
        data.setPayment(payment);

        wirecardCreditCardCheckEnrollmentResponseHandler.processResponse( abstractOrderModel, data);

        assertEquals("The payment is not correct", this.creditCardPaymentInfoModel, abstractOrderModel.getPaymentInfo());

    }

    @Test(expected = WirecardPaymenException.class)
    public void processCheckEnrollmentOtherErrorResponseTest() throws WirecardPaymenException {
        when(payment.getTransactionState()).thenReturn(TransactionState.FAILED);
        PaymentOperationData data = new PaymentOperationData();
        data.setPayment(payment);

        wirecardCreditCardCheckEnrollmentResponseHandler.processResponse( abstractOrderModel, data);
    }
}
