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

import com.wirecard.hybris.core.converter.data.PaymentModeData;
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
import com.wirecard.hybris.facades.WirecardCheckoutFacade;
import com.wirecard.hybris.facades.WirecardPaymentModeFacade;
import de.hybris.platform.acceleratorfacades.flow.impl.DefaultCheckoutFlowFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class DefaultWirecardCheckoutFacade extends DefaultCheckoutFlowFacade implements WirecardCheckoutFacade {

    private Converter<PaymentInfoModel, WirecardPaymentInfoData> wirecardPaymentInfoConverter;
    private Converter<PaymentModeModel, PaymentModeData> wirecardPaymentModeConverter;
    private Converter<CreditCardPaymentInfoModel, WirecardPaymentInfoData> wirecardCCPaymentInfoConverter;
    private WirecardPaymentService wirecardPaymentService;
    private WirecardPaymentModeService wirecardPaymentModeService;
    private WirecardTransactionService wirecardTransactionService;
    private WirecardPaymentModeFacade wirecardPaymentModeFacade;

    private CustomerEmailResolutionService customerEmailResolutionService;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    private CartService cartService;
    private FlexibleSearchService flexibleSearchService;
    private PaymentModeModel paymentModeModel;

    @Override
    public CartData getCheckoutCart() {
        final CartData cartData = super.getCheckoutCart();
        if (cartData != null) {
            cartData.setWirecardPaymentInfo(getWirecardPaymentDetails());
            cartData.setPaymentMode(getWirecardPaymentMode());
        }
        return cartData;
    }

    @Override
    public boolean hasNoPaymentInfo() {
        final CartData cartData = getCheckoutCart();
        return cartData == null || (cartData.getPaymentInfo() == null && cartData.getWirecardPaymentInfo() == null);
    }

    protected WirecardPaymentInfoData getWirecardPaymentDetails() {
        final CartModel cart = getCart();
        if (cart != null) {
            final PaymentInfoModel paymentInfo = cart.getPaymentInfo();

            if (paymentInfo != null) {
                return getWirecardPaymentInfoConverter().convert(paymentInfo);
            }

        }
        return null;
    }

    protected PaymentModeData getWirecardPaymentMode() {
        final CartModel cart = getCart();
        if (cart != null) {
            final PaymentModeModel paymentMode = cart.getPaymentMode();

            if (paymentMode != null) {
                return getWirecardPaymentModeConverter().convert(paymentMode);
            }

        }
        return null;
    }

    @Override
    public void setAuthentication(PaymentOperationData data) {

        CartModel cartModel = getCartService().getSessionCart();

        WirecardPaymentConfigurationModel configuration = wirecardPaymentConfigurationService.getConfiguration(cartModel.getPaymentMode());

        data.setWirecardAuthenticationModel(configuration.getAuthentication());

        WirecardAuthenticationModel fallbackAuthentication = configuration.getFallbackAuthentication();

        if (fallbackAuthentication != null) {
            data.setWirecardFallbackAuthenticationModel(fallbackAuthentication);
        }
    }

    public void setSamePaymentAdress() {
        CartModel cartModel = getCartService().getSessionCart();
        AddressModel deliveryAddress = cartModel.getDeliveryAddress();
        deliveryAddress.setBillingAddress(true);
        AddressModel paymentAddress = getModelService().clone(deliveryAddress);
        paymentAddress.setOwner(cartModel);
        cartModel.setPaymentAddress(paymentAddress);
        getModelService().saveAll(paymentAddress, cartModel);
    }

    public void setPaymentAddress(AddressData addressData) {
        CartModel cartModel = getCartService().getSessionCart();
        String email = getCustomerEmailResolutionService().getEmailForCustomer((CustomerModel) cartModel.getUser());
        AddressModel addressModel = createPaymentAddressModel(addressData, cartModel);
        addressModel.setEmail(email);
        cartModel.setPaymentAddress(addressModel);
        getModelService().saveAll(addressModel, cartModel);
    }

    private AddressModel createPaymentAddressModel(final AddressData addressData, final CartModel cartModel) {
        final AddressModel addressModel = getModelService().create(AddressModel.class);
        getAddressReversePopulator().populate(addressData, addressModel);
        addressModel.setOwner(cartModel);
        return addressModel;
    }


    /**
     * Return
     *
     * @return if the cart has payment authorized
     */
    public boolean isPaymentAthorized() {
        final CartModel cart = getCart();
        return cart != null && (wirecardTransactionService.lookForAcceptedTransactions(cart, PaymentTransactionType.AUTHORIZATION)
            || wirecardTransactionService.lookForAcceptedTransactions(cart, PaymentTransactionType.DEBIT));
    }

    public WirecardRequestData getWireCardRequestData(String paymentModeCode) {

        PaymentModeModel paymentMode = new PaymentModeModel();
        paymentMode.setCode(paymentModeCode);
        paymentMode = getFlexibleSearchService().getModelByExample(paymentMode);

        CartModel cart = getCartService().getSessionCart();

        TransactionType transactionType = TransactionType.AUTHORIZATION;
        PaymentMethodName paymentMethodName = PaymentMethodName.CREDITCARD;

        return wirecardPaymentModeFacade.getSeamlessFormData(cart, transactionType, paymentMethodName, paymentMode);
    }

    @Override
    public void storePares(String pares) {
        CartModel sessionCart = cartService.getSessionCart();
        getWirecardPaymentService().storePares(sessionCart, pares);
    }

    public void setPaymentMode(String paymentCode) {
        CartModel sessionCart = cartService.getSessionCart();
        PaymentModeModel paymentMode = wirecardPaymentModeService.getPaymentModeByCode(paymentCode);
        wirecardPaymentModeService.storePaymentMode(sessionCart, paymentMode);
        this.paymentModeModel = paymentMode;
    }

    public PaymentModeModel getPaymentMode() {
        return paymentModeModel;
    }

    public List<WirecardPaymentInfoData> getWirecardPaymentInfos(final boolean saved) {
        final CustomerModel currentCustomer = getCurrentUserForCheckout();

        final List<CreditCardPaymentInfoModel> creditCards = getCustomerAccountService().getCreditCardPaymentInfos(currentCustomer,
                                                                                                                   saved);
        final List<WirecardPaymentInfoData> ccPaymentInfos = new ArrayList<>();
        final PaymentInfoModel defaultPaymentInfoModel = currentCustomer.getDefaultPaymentInfo();
        for (final CreditCardPaymentInfoModel ccPaymentInfoModel : creditCards) {
            final WirecardPaymentInfoData paymentInfoData = getWirecardCCPaymentInfoConverter().convert(ccPaymentInfoModel);
            if (ccPaymentInfoModel.equals(defaultPaymentInfoModel)) {
                paymentInfoData.setDefaultPaymentInfo(true);
                ccPaymentInfos.add(0, paymentInfoData);
            } else {
                ccPaymentInfos.add(paymentInfoData);
            }
        }
        return ccPaymentInfos;
    }

    public String getWirecardBaseURL(String paymentMethodCode) {
        return getWirecardPaymentConfigurationService().getBaseURL(paymentMethodCode);
    }

    protected WirecardPaymentService getWirecardPaymentService() {
        return wirecardPaymentService;
    }

    @Required
    public void setWirecardPaymentService(WirecardPaymentService wirecardPaymentService) {
        this.wirecardPaymentService = wirecardPaymentService;
    }

    protected CustomerEmailResolutionService getCustomerEmailResolutionService() {
        return customerEmailResolutionService;
    }

    @Required
    public void setCustomerEmailResolutionService(CustomerEmailResolutionService customerEmailResolutionService) {
        this.customerEmailResolutionService = customerEmailResolutionService;
    }

    @Override
    public CartService getCartService() {
        return cartService;
    }

    @Override
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    protected Converter<PaymentInfoModel, WirecardPaymentInfoData> getWirecardPaymentInfoConverter() {
        return wirecardPaymentInfoConverter;
    }

    @Required
    public void setWirecardPaymentInfoConverter(Converter<PaymentInfoModel, WirecardPaymentInfoData> wirecardPaymentInfoConverter) {
        this.wirecardPaymentInfoConverter = wirecardPaymentInfoConverter;
    }

    protected Converter<CreditCardPaymentInfoModel, WirecardPaymentInfoData> getWirecardCCPaymentInfoConverter() {
        return wirecardCCPaymentInfoConverter;
    }

    @Required
    public void setWirecardCCPaymentInfoConverter(Converter<CreditCardPaymentInfoModel,
        WirecardPaymentInfoData> wirecardCCPaymentInfoConverter) {
        this.wirecardCCPaymentInfoConverter = wirecardCCPaymentInfoConverter;
    }

    protected WirecardPaymentModeService getWirecardPaymentModeService() {
        return wirecardPaymentModeService;
    }

    @Required
    public void setWirecardPaymentModeService(WirecardPaymentModeService wirecardPaymentModeService) {
        this.wirecardPaymentModeService = wirecardPaymentModeService;
    }

    protected WirecardPaymentModeFacade getWirecardPaymentModeFacade() {
        return wirecardPaymentModeFacade;
    }

    @Required
    public void setWirecardPaymentModeFacade(WirecardPaymentModeFacade wirecardPaymentModeFacade) {
        this.wirecardPaymentModeFacade = wirecardPaymentModeFacade;
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }

    protected Converter<PaymentModeModel, PaymentModeData> getWirecardPaymentModeConverter() {
        return wirecardPaymentModeConverter;
    }

    @Required
    public void setWirecardPaymentModeConverter(Converter<PaymentModeModel, PaymentModeData> wirecardPaymentModeConverter) {
        this.wirecardPaymentModeConverter = wirecardPaymentModeConverter;
    }
}
