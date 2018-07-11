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

package com.wirecard.hybris.facades.impl;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethod;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.PaymentMethods;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.operation.PaymentOperation;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.WirecardPaymentModeService;
import com.wirecard.hybris.core.strategy.PaymentOperationStrategy;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.paymentstandard.model.StandardPaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
public class DefaultWirecardHopPaymentOperationsFacadeTest {

    private DefaultWirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    @Mock
    private CartService cartService;
    @Mock
    private UserService userService;
    @Mock
    private WirecardPaymentModeService wirecardPaymentModeService;
    @Mock
    private PaymentOperationStrategy paymentOperationStrategy;
    @Mock
    private PaymentOperation paymentOperation;
    @Mock
    private UserModel userModel;
    @Mock
    private CartModel cartModel;
    @Mock
    private StandardPaymentModeModel standardPaymentModeModel;
    @Mock
    private Payment payment;
    @Mock
    private Converter<AddressData,AddressModel> addressConverter;
    @Mock
    private AddressData addressData;
    @Mock
    private AddressModel addressModel;
    @Mock
    private PaymentOperationData paymentOperationData;
    @Mock
    private PaymentMethods paymentMethods;
    @Mock
    private List<PaymentMethod> paymentMethodList;
    @Mock
    private PaymentMethod paymentMethod;


    private String operation;

    @Before
    public void setup() throws WirecardPaymenException {

        MockitoAnnotations.initMocks(this);

        wirecardHopPaymentOperationsFacade = new DefaultWirecardHopPaymentOperationsFacade();
        wirecardHopPaymentOperationsFacade.setPaymentOperationStrategy(paymentOperationStrategy);
        wirecardHopPaymentOperationsFacade.setUserService(userService);
        wirecardHopPaymentOperationsFacade.setCartService(cartService);
        wirecardHopPaymentOperationsFacade.setAddressConverter(addressConverter);
        when(paymentOperationStrategy.getOperation(standardPaymentModeModel, operation))
            .thenReturn(paymentOperation);
        when(paymentOperation.doOperation(cartModel, paymentOperationData))
            .thenReturn(payment);
        when(wirecardPaymentModeService.getPaymentModeByCode(StringUtils.EMPTY))
            .thenReturn(standardPaymentModeModel);
        when(userService.getCurrentUser())
            .thenReturn(userModel);
        when(cartService.getSessionCart())
            .thenReturn(cartModel);
        when(cartModel.getPaymentMode())
            .thenReturn(standardPaymentModeModel);
        when(addressConverter.convert(addressData))
            .thenReturn(addressModel);
        when(payment.getPaymentMethods())
            .thenReturn(paymentMethods);
        when(paymentMethods.getPaymentMethod())
            .thenReturn(paymentMethodList);
        when(paymentMethodList.get(0))
            .thenReturn(paymentMethod);
        when(paymentMethod.getName())
            .thenReturn(PaymentMethodName.CREDITCARD);
        when(payment.getTransactionType())
            .thenReturn(TransactionType.AUTHORIZATION);
        when(payment.getTransactionState())
            .thenReturn(TransactionState.SUCCESS);
    }


    @Test
    public void executePaymentOperationTest() {

        Payment payment = null;
        try {
            payment = wirecardHopPaymentOperationsFacade.executePaymentOperation(operation, paymentOperationData);
        } catch (WirecardPaymenException e) {
            e.printStackTrace();
        }
        //compare both datas
        assertEquals("The payment is not correct", payment, this.payment);


    }

}
