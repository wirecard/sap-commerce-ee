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

import com.wirecard.hybris.core.converter.xml.PaymentConverter;
import com.wirecard.hybris.core.data.types.MerchantAccountId;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.operation.impl.DefaultPaymentOperation;
import com.wirecard.hybris.core.payment.command.PaymentCommand;
import com.wirecard.hybris.core.payment.response.ResponseHandler;
import com.wirecard.hybris.core.payment.transaction.WirecardTransactionData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.core.strategy.impl.DefaultTransactionTypeStrategy;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentOperationTest {

    @InjectMocks
    private DefaultPaymentOperation paymentOperation;

    @Mock
    private Converter<AbstractOrderModel, Payment> paymentDataConverter;
    @Mock
    private PaymentConverter paymentConverter;
    @Mock
    private PaymentCommand paymentCommand;
    @Mock
    private ResponseHandler responseHandler;
    @Mock
    private DefaultTransactionTypeStrategy transactionTypeStrategy;
    @Mock
    private CartModel order;
    @Mock
    private WirecardTransactionData transactionData;
    @Mock
    private Payment requestData;
    @Mock
    private Payment payment;
    @InjectMocks
    private PaymentOperationData data;
    @Mock
    private WirecardTransactionService wirecardTransactionService;
    @Mock
    private AbstractOrderModel abstractOrderModel;
    @Mock
    private PaymentTransactionModel paymentTransactionModel;

    @Mock
    private PaymentModeModel paymentModeModel;
    @Mock
    private WirecardAuthenticationModel authenticationModel;
    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    private String requestXML;

    @Before
    public void setup() throws WirecardPaymenException {

        requestXML = "<XML />";

        MerchantAccountId merchantAccountId = new MerchantAccountId();
        when(paymentDataConverter.convert(order)).thenReturn(requestData);
        when(requestData.getMerchantAccountId()).thenReturn(merchantAccountId);
        when(paymentConverter.convertDataToXML(requestData)).thenReturn(requestXML);
        when(paymentCommand.execute(requestData, authenticationModel)).thenReturn(payment);
        when(responseHandler.getOrder(order)).thenReturn(abstractOrderModel);
        when(transactionTypeStrategy.getPaymentTransactionType(transactionData)).thenReturn(PaymentTransactionType.AUTHORIZATION);
        when(responseHandler.getTransactionAmount(order)).thenReturn(BigDecimal.ONE);
        when(abstractOrderModel.getPaymentMode())
            .thenReturn(paymentModeModel);
        when(paymentTransactionModel.getAuthentication())
            .thenReturn(authenticationModel);
        when(authenticationModel.getMaid())
            .thenReturn(StringUtils.EMPTY);
        when(authenticationModel.getMaid())
            .thenReturn(StringUtils.EMPTY);

    }

    @Test
    public void testPaymentOperation() throws WirecardPaymenException {
        paymentOperation.doOperation(order, data);
    }
}
