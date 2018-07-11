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

package com.wirecard.hybris.facades;

import com.wirecard.hybris.core.converter.data.WirecardPaymentInfoData;
import com.wirecard.hybris.core.data.WirecardRequestData;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardPaymentModeService;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.facades.impl.DefaultWirecardCheckoutFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardCheckoutFacadeTest {

    @InjectMocks
    private DefaultWirecardCheckoutFacade defaultWirecardCheckoutFacade;

    @Mock
    private Converter<PaymentInfoModel, WirecardPaymentInfoData> wirecardPaymentInfoConverter;
    @Mock
    private WirecardPaymentService wirecardPaymentService;
    @Mock
    private WirecardTransactionService wirecardTransactionService;
    @Mock
    private CustomerEmailResolutionService customerEmailResolutionService;
    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    @Mock
    private CartService cartService;
    @Mock
    private CartFacade cartFacade;
    @Mock
    private WirecardPaymentModeFacade wirecardPaymentModeFacade;
    @Mock
    private WirecardPaymentModeService wirecardPaymentModeService;
    @Mock
    private FlexibleSearchService flexibleSearchService;
    @Mock
    private ModelService modelService;
    @Mock
    private AddressReversePopulator addressReversePopulator;

    private CartModel cartModel;
    private CartData expectedCartData;
    private WirecardPaymentInfoData paymentInfoData;

    @Before
    public void setup() {
        PaymentInfoModel paymentInfoModel = new PaymentInfoModel();
        cartModel = new CartModel();
        cartModel.setPaymentInfo(paymentInfoModel);
        expectedCartData = new CartData();
        paymentInfoData = new WirecardPaymentInfoData();
        defaultWirecardCheckoutFacade.setWirecardPaymentService(wirecardPaymentService);
        defaultWirecardCheckoutFacade.setWirecardPaymentInfoConverter(wirecardPaymentInfoConverter);
        defaultWirecardCheckoutFacade.setAddressReversePopulator(addressReversePopulator);

        when(cartFacade.hasSessionCart()).thenReturn(true);
        when(cartFacade.getSessionCart()).thenReturn(expectedCartData);
        when(cartService.getSessionCart()).thenReturn(cartModel);
        when(wirecardPaymentInfoConverter.convert(paymentInfoModel)).thenReturn(paymentInfoData);

    }

    @Test
    public void testCheckoutCart() {
        CartData checkoutCart = defaultWirecardCheckoutFacade.getCheckoutCart();

        assertEquals("Checkout cart does not match", expectedCartData, checkoutCart);
        assertEquals("Payment info does not match", expectedCartData.getWirecardPaymentInfo(), paymentInfoData);
    }

    @Test
    public void testHasNoPaymentInfo() {
        boolean hasNoPaymentInfo = defaultWirecardCheckoutFacade.hasNoPaymentInfo();

        assertFalse("Cart has payment info", hasNoPaymentInfo);
    }

    @Test
    public void testSetAuthentication() {
        PaymentModeModel paymentMode = new PaymentModeModel();
        cartModel.setPaymentMode(paymentMode);
        WirecardPaymentConfigurationModel configuration = new WirecardPaymentConfigurationModel();
        WirecardAuthenticationModel expectedAuthentication = new WirecardAuthenticationModel();
        WirecardAuthenticationModel expectedFallbackAuthentication = new WirecardAuthenticationModel();
        configuration.setAuthentication(expectedAuthentication);
        configuration.setFallbackAuthentication(expectedFallbackAuthentication);

        when(wirecardPaymentConfigurationService.getConfiguration(paymentMode)).thenReturn(configuration);

        PaymentOperationData data = new PaymentOperationData();
        defaultWirecardCheckoutFacade.setAuthentication(data);

        assertEquals("Authentication does not match", expectedAuthentication, data.getWirecardAuthenticationModel());
        assertEquals("Fallback authentication does not match",
                     expectedFallbackAuthentication, data.getWirecardFallbackAuthenticationModel());
    }

    @Test
    public void testSetPaymentAdress() {
        String email = "test@wirecard.com";
        AddressModel addressModel = new AddressModel();
        CustomerModel user = new CustomerModel();
        cartModel.setUser(user);

        when(customerEmailResolutionService.getEmailForCustomer(user)).thenReturn(email);
        when(modelService.create(AddressModel.class)).thenReturn(addressModel);

        AddressData addressData = new AddressData();
        defaultWirecardCheckoutFacade.setPaymentAddress(addressData);

        assertEquals("Payment address does not match", addressModel, cartModel.getPaymentAddress());
        assertEquals("Payment address owner does not match", cartModel, addressModel.getOwner());
        assertEquals("Email does not match", email, addressModel.getEmail());
    }

    @Test
    public void testIsPaymentAthorized() {
        when(wirecardTransactionService.lookForAcceptedTransactions(cartModel, PaymentTransactionType.AUTHORIZATION)).thenReturn(true);
        assertTrue("Payment is not authorized", defaultWirecardCheckoutFacade.isPaymentAthorized());

        when(wirecardTransactionService.lookForAcceptedTransactions(cartModel, PaymentTransactionType.AUTHORIZATION)).thenReturn(false);
        assertFalse("Payment is authorized", defaultWirecardCheckoutFacade.isPaymentAthorized());
    }

    @Test
    public void testGetWireCardRequestData() {
        WirecardRequestData expectedWirecardRequestData = new WirecardRequestData();
        PaymentModeModel paymentModeModel = new PaymentModeModel();
        when(flexibleSearchService.getModelByExample(Mockito.any(PaymentModeModel.class))).thenReturn(paymentModeModel);
        when(wirecardPaymentModeFacade.getSeamlessFormData(cartModel,
                                                           TransactionType.AUTHORIZATION,
                                                           PaymentMethodName.CREDITCARD,
                                                           paymentModeModel))
            .thenReturn(expectedWirecardRequestData);

        WirecardRequestData wireCardRequestData = defaultWirecardCheckoutFacade.getWireCardRequestData("paymentMode");

        assertEquals("Wirecard request data does not match", expectedWirecardRequestData, wireCardRequestData);
    }

    @Test
    public void testStorePares() {
        defaultWirecardCheckoutFacade.storePares("pares");
    }

    @Test
    public void testSetPaymentMode() {
        String paymentCode = "paymentMode";
        PaymentModeModel paymentMode = new PaymentModeModel();
        when(wirecardPaymentModeService.getPaymentModeByCode(paymentCode)).thenReturn(paymentMode);

        defaultWirecardCheckoutFacade.setPaymentMode(paymentCode);
    }
}
