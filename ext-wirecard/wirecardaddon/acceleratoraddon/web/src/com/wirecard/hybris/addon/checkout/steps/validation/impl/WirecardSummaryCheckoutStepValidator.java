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

package com.wirecard.hybris.addon.checkout.steps.validation.impl;

import com.wirecard.hybris.core.enums.WirecardTransactionType;
import com.wirecard.hybris.facades.impl.DefaultWirecardCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.AbstractCheckoutStepValidator;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.data.CartData;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


public class WirecardSummaryCheckoutStepValidator extends AbstractCheckoutStepValidator {

    private static final Logger LOGGER = Logger.getLogger(WirecardSummaryCheckoutStepValidator.class);

    @Override
    public ValidationResults validateOnEnter(final RedirectAttributes redirectAttributes) {
        final ValidationResults cartResult = checkCartAndDelivery(redirectAttributes);
        if (cartResult != null) {
            return cartResult;
        }

        final ValidationResults paymentResult = checkPaymentMethodAndPickup(redirectAttributes);
        if (paymentResult != null) {
            return paymentResult;
        }

        return ValidationResults.SUCCESS;
    }

    protected ValidationResults checkPaymentMethodAndPickup(final RedirectAttributes redirectAttributes) {

        if (getCheckoutFlowFacade().hasNoPaymentInfo() && !isPurchase(((DefaultWirecardCheckoutFacade) getCheckoutFacade()).getPaymentMode()
            .getTransactionType())) {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
                                           "checkout.multi.paymentDetails.notprovided");
            return ValidationResults.REDIRECT_TO_PAYMENT_METHOD;
        }

        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        if (!getCheckoutFacade().hasShippingItems()) {
            cartData.setDeliveryAddress(null);
        }

        if (!getCheckoutFacade().hasPickUpItems() && "pickup".equals(cartData.getDeliveryMode().getCode())) {
            return ValidationResults.REDIRECT_TO_PICKUP_LOCATION;
        }
        return null;
    }

    private boolean isPurchase(WirecardTransactionType transactionType) {
        return WirecardTransactionType.PURCHASE.equals(transactionType)
            || WirecardTransactionType.PURCHASE_WITH_HOP.equals(transactionType);
    }

    protected ValidationResults checkCartAndDelivery(final RedirectAttributes redirectAttributes) {
        if (!getCheckoutFlowFacade().hasValidCart()) {
            LOGGER.info("Missing, empty or unsupported cart");
            return ValidationResults.REDIRECT_TO_CART;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
                                           "checkout.multi.deliveryAddress.notprovided");
            return ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.INFO_MESSAGES_HOLDER,
                                           "checkout.multi.deliveryMethod.notprovided");
            return ValidationResults.REDIRECT_TO_DELIVERY_METHOD;
        }
        return null;
    }
}
