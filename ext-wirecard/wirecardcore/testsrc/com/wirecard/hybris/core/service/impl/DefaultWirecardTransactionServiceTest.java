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
import com.wirecard.hybris.core.data.types.CardToken;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.core.data.types.Statuses;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.payment.transaction.WirecardTransactionData;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.order.payment.PaypalPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WirecardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardTransactionServiceTest {

    @InjectMocks
    private DefaultWirecardTransactionService defaultWirecardTransactionService;

    @Mock
    private WirecardPaymentService wirecardPaymentService;
    @Mock
    private WirecardTransactionData wirecardTransactionData;

    private String checkoutPaymentType;
    @Mock
    private ModelService modelService;
    @Mock
    private FlexibleSearchService flexibleSearchService;
    @Mock
    private AbstractOrderModel order;
    @Mock
    private UserModel user;
    @Mock
    private PaymentTransactionModel transaction1;
    private PaymentTransactionModel newTransaction;

    @Mock
    private PaymentConverter paymentConverter;
    @Mock
    private PaymentTransactionEntryModel newTransactionEntry;
    @Mock
    private AddressModel userAddress;
    @Mock
    private PaymentModeModel paymentModeModel;
    @Mock
    private CardToken cardToken;
    @Mock
    private Statuses statuses;
    @Mock
    private Status status;
    @Mock
    private PaymentTransactionEntryModel transactionEntry1;
    @Mock
    private Payment authorization;
    @Mock
    private Payment authorizationRejected;
    @Mock
    private Payment capture;
    @Mock
    private Payment captureRejected;
    @Mock
    private DefaultMediaService mediaService;
    @Mock
    private MediaFolderModel folder;

    private WirecardPaymentInfoModel newPaymentInfo;
    private CatalogUnawareMediaModel catalogUnawareMediaModel;

    private String provider1;
    private String provider2;
    private String transactionId;
    private String userId;
    private String tokenId;
    private BigDecimal transactionAmount;
    private String providerTransactionId;
    private String providerTransactionReferenceId;
    private String subscriptionID;
    private String description;

    @Before
    public void setup() throws WirecardPaymenException {
        MockitoAnnotations.initMocks(this);

        checkoutPaymentType = "checkoutPaymentType";
        provider1 = "TestPaymentProvider";
        provider2 = "TestPaymentProvider2";
        tokenId = "tokenId";
        transactionAmount = BigDecimal.ONE;
        transactionId = "001";
        userId = "U12345";
        providerTransactionId = "providerTransactionId";
        providerTransactionReferenceId = "providerTransactionReferenceId";
        subscriptionID = "TR01";
        description = "Description";

        newTransaction = new PaymentTransactionModel();
        newTransactionEntry = new PaymentTransactionEntryModel();
        newPaymentInfo = new PaypalPaymentInfoModel();
        catalogUnawareMediaModel = new CatalogUnawareMediaModel();

        when(user.getUid()).thenReturn(userId);
        when(order.getUser()).thenReturn(user);
        when(order.getPaymentAddress()).thenReturn(userAddress);
        when(authorization.getCardToken()).thenReturn(cardToken);
        when(cardToken.getTokenId()).thenReturn(tokenId);
        when(authorization.getStatuses()).thenReturn(statuses);
        when(statuses.getStatus())
            .thenReturn(Collections.singletonList(status));
        when(status.getDescription()).thenReturn(description);
        when(status.getProviderTransactionId()).thenReturn(providerTransactionId);
        when(authorization.getProviderTransactionReferenceId()).thenReturn(providerTransactionReferenceId);

        when(wirecardTransactionData.getAbstractOrderModel()).thenReturn(order);
        when(wirecardTransactionData.getCheckoutPaymentType()).thenReturn(checkoutPaymentType);
        when(wirecardTransactionData.getPaymentTransactionType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(wirecardTransactionData.getResponse()).thenReturn(authorization);
        when(wirecardTransactionData.getTransactionId()).thenReturn(transactionId);
        when(wirecardTransactionData.getTransactionAmount()).thenReturn(transactionAmount);

        when(wirecardPaymentService.getOrderStatus(authorization)).thenReturn(OrderStatus.PAYMENT_CAPTURED);

        when(modelService.create(PaymentTransactionModel.class)).thenReturn(newTransaction);
        when(transaction1.getPaymentProvider()).thenReturn(provider1);
        when(transaction1.getEntries()).thenReturn(Collections.singletonList(transactionEntry1));
        when(modelService.create(PaymentTransactionEntryModel.class)).thenReturn(newTransactionEntry);
        when(modelService.create(PaypalPaymentInfoModel.class)).thenReturn(newPaymentInfo);
        when(modelService.create(CatalogUnawareMediaModel.class)).thenReturn(catalogUnawareMediaModel);
        when(flexibleSearchService.getModelByExample(paymentModeModel)).thenReturn(paymentModeModel);
        when(transaction1.getPaymentProvider()).thenReturn(provider1);

        when(order.getPaymentMode())
            .thenReturn(paymentModeModel);
        when(order.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction1));
        when(paymentModeModel.getCode()).thenReturn(provider1);

        when(transaction1.getPaymentProvider()).thenReturn(provider1);
        when(transaction1.getEntries()).thenReturn(Collections.singletonList(transactionEntry1));

        when(transactionEntry1.getType()).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(transactionEntry1.getTransactionStatus()).thenReturn(TransactionStatus.ACCEPTED.name());
        when(transactionEntry1.getSubscriptionID()).thenReturn(subscriptionID);

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

        when(paymentConverter.convertDataToXML(authorization)).thenReturn("xml");
        when(mediaService.getFolder(Mockito.anyString())).thenReturn(folder);


    }


    @Test
    public void testLogTransactionData() {
        when(order.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction1));
        PaymentTransactionEntryModel transaction = defaultWirecardTransactionService.logTransactionData(wirecardTransactionData);

        assertEquals("Transaction type does not match", PaymentTransactionType.AUTHORIZATION, transaction.getType());
        assertEquals("Transaction status does not match", TransactionStatus.ACCEPTED.toString(), transaction.getTransactionStatus());
        assertEquals("Transaction amount does not match", transactionAmount, transaction.getAmount());
        assertEquals("Transaction subscriptionId does not match", transactionId, transaction.getSubscriptionID());
    }

    @Test
    public void testValidCompatibleTransactionSearch() {
        when(order.getPaymentTransactions()).thenReturn(Collections.singletonList(transaction1));
        PaymentTransactionModel compatibleTransaction = defaultWirecardTransactionService
            .lookForCompatibleTransactions(order, provider1);

        assertNotNull("Should have found a compatible transaction", compatibleTransaction);
        assertEquals("Compatible transaction does not match", transaction1, compatibleTransaction);
    }

    @Test
    public void lookForAuthorizationTransactions() {

        Assert.assertTrue(defaultWirecardTransactionService.lookForAcceptedTransactions(order, PaymentTransactionType.AUTHORIZATION));
    }

    @Test
    public void getPaymentTransactionTest() {

        PaymentTransactionModel transactionModel = defaultWirecardTransactionService.getPaymentTransaction(order);

        assertEquals("Payment transaction", transaction1, transactionModel);
    }

    @Test
    public void getTransactionAuthorizationStatusAcceptedTest() {

        assertEquals("Transaction is accepted",
                     TransactionStatus.ACCEPTED, defaultWirecardTransactionService.getTransactionStatus(authorization));
    }

    @Test
    public void getTransactionAuthorizationStatusRejectedTest() {

        assertEquals("Transaction is rejected",
                     TransactionStatus.REJECTED, defaultWirecardTransactionService.getTransactionStatus(authorizationRejected));
    }


    @Test
    public void getTransactionCaptureStatusAcceptedTest() {

        assertEquals("Transaction is accepted",
                     TransactionStatus.ACCEPTED, defaultWirecardTransactionService.getTransactionStatus(capture));
    }

    @Test
    public void getTransactionCaptureStatusRejectedTest() {

        assertEquals("Transaction is rejected",
                     TransactionStatus.REJECTED, defaultWirecardTransactionService.getTransactionStatus(captureRejected));
    }


}
