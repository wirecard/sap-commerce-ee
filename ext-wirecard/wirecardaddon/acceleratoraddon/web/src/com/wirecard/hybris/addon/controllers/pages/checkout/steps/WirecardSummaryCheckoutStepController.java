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

package com.wirecard.hybris.addon.controllers.pages.checkout.steps;

import com.wirecard.hybris.addon.controllers.WirecardaddonControllerConstants;
import com.wirecard.hybris.core.constants.WirecardPaymentTransactionConstants;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethod;
import com.wirecard.hybris.core.enums.WirecardTransactionType;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.exception.WirecardNotEnrolledException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import com.wirecard.hybris.facades.impl.DefaultWirecardCheckoutFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.PlaceOrderForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.AdapterException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping(value = "/checkout/multi/wirecard/summary")
public class WirecardSummaryCheckoutStepController extends AbstractCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WirecardSummaryCheckoutStepController.class);
    private static final String SUMMARY = "summary";
    private static final String GENERAL_ERROR = "checkout.multi.paymentMethod.addPaymentDetails.generalError";
    private static final String HOP_DEBIT_OPERATION_LOG_MSG = "executing debit payment operation with HOP";
    private static final String DEBIT_OPERATION_LOG_MSG = "executing debit payment operation without HOP";
    private static final String REDIRECT_LOG_MSG = "redirecting to %s";
    private static final String CHECKOUT_MULTI_ERROR_ANOTHER = "checkout.multi.error.another";
    @Resource(name = "wirecardHopPaymentOperationsFacade")
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;
    @Resource(name = "wirecardPaymentConfigurationService")
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @RequestMapping(value = "/checkTransactionType")
    @RequireHardLogIn
    public String checkTransactionType(@ModelAttribute("placeOrderForm") final PlaceOrderForm placeOrderForm,
                                       final Model model,
                                       final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        if (validateForm(placeOrderForm, model)) {
            return enterStep(model, redirectModel);
        }

        WirecardTransactionType wirecardTransactionType = getCheckoutFacade().getPaymentMode().getTransactionType();
        if (WirecardTransactionType.PURCHASE_WITH_HOP.equals(wirecardTransactionType)) {
            return executeOperationHopDebit(model, redirectModel);
        } else if (WirecardTransactionType.PURCHASE.equals(wirecardTransactionType)) {
            return executeOperationDebit(model, redirectModel);
        }

        return REDIRECT_PREFIX + wirecardPaymentConfigurationService.getPlaceOrderURL();

    }

    @RequestMapping(value = "/placeOrder")
    @PreValidateQuoteCheckoutStep
    @RequireHardLogIn
    public String placeOrder(final Model model,
                             final HttpServletRequest request, final RedirectAttributes redirectModel)
        throws CMSItemNotFoundException, InvalidCartException { // NOSONAR

        if (validateOrder(model)) {
            return enterStep(model, redirectModel);
        }

        //Validate the cart
        if (validateCart(redirectModel)) {
            // Invalid cart. Bounce back to the cart page.
            return REDIRECT_PREFIX + "/cart";
        }

        // authorize, if failure occurs don't allow to place the order
        boolean isPaymentUthorized = false;
        try {
            isPaymentUthorized = getCheckoutFacade().isPaymentAthorized();
        } catch (final AdapterException exa) {
            // handle a case where a wrong paymentProvider configurations on the store see getCommerceCheckoutService().getPaymentProvider()
            LOGGER.error(exa.getMessage(), exa);
        }
        if (!isPaymentUthorized) {
            GlobalMessages.addErrorMessage(model, "checkout.error.authorization.failed");
            return enterStep(model, redirectModel);
        }

        final OrderData orderData;
        try {
            orderData = getCheckoutFacade().placeOrder();
        } catch (final InvalidCartException e) {
            LOGGER.error("Failed to place Order", e);
            GlobalMessages.addErrorMessage(model, "checkout.placeOrder.failed");
            return enterStep(model, redirectModel);
        }

        return redirectToOrderConfirmationPage(orderData);
    }

    /**
     * Validates the order form before to filter out invalid order states
     *
     * @param model
     *     A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean validateOrder(final Model model) {

        boolean invalid = false;

        if (getCheckoutFlowFacade().hasNoDeliveryAddress()) {
            GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
            invalid = true;
        }

        if (getCheckoutFlowFacade().hasNoDeliveryMode()) {
            GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
            invalid = true;
        }

        if (getCheckoutFlowFacade().hasNoPaymentInfo()) {
            GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
            invalid = true;
        }

        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        if (!getCheckoutFacade().containsTaxValues()) {
            LOGGER.error(String.format(
                "Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't "
                    + "continue",
                cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
            invalid = true;
        }

        if (!cartData.isCalculated()) {
            LOGGER.error(
                String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
            GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
            invalid = true;
        }

        return invalid;
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    @RequireHardLogIn
    @PreValidateQuoteCheckoutStep
    @PreValidateCheckoutStep(checkoutStep = SUMMARY)
    public String enterStep(final Model model,
                            final RedirectAttributes redirectAttributes)
        throws CMSItemNotFoundException {
        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        if (cartData.getEntries() != null && !cartData.getEntries().isEmpty()) {
            for (final OrderEntryData entry : cartData.getEntries()) {
                final String productCode = entry.getProduct().getCode();
                final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode, Arrays.asList(
                    ProductOption.BASIC, ProductOption.PRICE, ProductOption.VARIANT_MATRIX_BASE, ProductOption.PRICE_RANGE));
                entry.setProduct(product);
            }
        }

        model.addAttribute("cartData", cartData);
        model.addAttribute("allItems", cartData.getEntries());
        model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
        model.addAttribute("deliveryMode", cartData.getDeliveryMode());
        model.addAttribute("paymentInfo", cartData.getWirecardPaymentInfo());

        // Only request the security code if the SubscriptionPciOption is set to Default.
        final boolean requestSecurityCode = CheckoutPciOptionEnum.DEFAULT
            .equals(getCheckoutFlowFacade().getSubscriptionPciOption());
        model.addAttribute("requestSecurityCode", requestSecurityCode);

        model.addAttribute(new PlaceOrderForm());

        storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                           getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
        model.addAttribute("metaRobots", "noindex,nofollow");
        setCheckoutStepLinksForModel(model, getCheckoutStep());
        return WirecardaddonControllerConstants.checkoutSummaryPage;
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    @Override
    protected DefaultWirecardCheckoutFacade getCheckoutFacade() {
        return (DefaultWirecardCheckoutFacade) super.getCheckoutFacade();
    }

    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(SUMMARY);
    }

    /**
     * Validates the form before to filter out invalid order states
     *
     * @param placeOrderForm
     *     The spring form of the order being submitted
     * @param model
     *     A spring Model
     * @return True if the order form is invalid and false if everything is valid.
     */
    protected boolean validateForm(final PlaceOrderForm placeOrderForm, final Model model) {

        if (!placeOrderForm.isTermsCheck()) {
            GlobalMessages.addErrorMessage(model, "checkout.error.terms.not.accepted");
            return true;
        }

        final String securityCode = placeOrderForm.getSecurityCode();

        if (CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade().getSubscriptionPciOption())
            && StringUtils.isBlank(securityCode)) {
            GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
            return true;
        }
        return false;
    }

    private String executeOperationHopDebit(final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        LOGGER.info(HOP_DEBIT_OPERATION_LOG_MSG);

        try {
            PaymentOperationData data = new PaymentOperationData();
            getCheckoutFacade().setAuthentication(data);
            String bic = wirecardHopPaymentOperationsFacade.getIdealBic();

            if (StringUtils.isNotEmpty(bic)) {
                data.setBic(bic);
            }

            Payment payment =
                getWirecardHopPaymentOperationsFacade().executePaymentOperation(WirecardPaymentTransactionConstants.DEBIT, data);

            if (payment == null) {
                GlobalMessages.addErrorMessage(model, GENERAL_ERROR);
                return enterStep(model, redirectModel);
            }

            String responsePage = payment.getPaymentMethods().getPaymentMethod().stream()
                                         .map(PaymentMethod::getUrl)
                                         .findAny()
                                         .map(url -> REDIRECT_PREFIX + url)
                                         .orElse(REDIRECT_URL_ERROR);

            LOGGER.info(String.format(REDIRECT_LOG_MSG, responsePage));

            return responsePage;


        } catch (WirecardNotEnrolledException e) {
            LOGGER.info("Not enrolled", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, "checkout.wirecard.payment.notenrolled");
            return back(redirectModel);
        } catch (WirecardPaymenException e) {
            LOGGER.error("Payment failed", e);
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, e.getMessage());
            GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, CHECKOUT_MULTI_ERROR_ANOTHER);
            return back(redirectModel);
        }
    }

    private String executeOperationDebit(final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        LOGGER.info(DEBIT_OPERATION_LOG_MSG);
        try {

            PaymentOperationData data = new PaymentOperationData();
            getCheckoutFacade().setAuthentication(data);

            Payment payment =
                getWirecardHopPaymentOperationsFacade().executePaymentOperation(WirecardPaymentTransactionConstants.DEBIT, data);
            if (payment == null) {
                GlobalMessages.addErrorMessage(model, GENERAL_ERROR);
                return enterStep(model, redirectModel);
            }

            return REDIRECT_PREFIX + wirecardPaymentConfigurationService.getPlaceOrderURL();


        } catch (WirecardNotEnrolledException e) {
            LOGGER.info("Not enrolled", e);
            GlobalMessages.addErrorMessage(model, "checkout.wirecard.payment.notenrolled");
            return enterStep(model, redirectModel);
        } catch (WirecardPaymenException e) {
            LOGGER.error("Payment failed", e);
            GlobalMessages.addErrorMessage(model, "checkout.wirecard.payment.failed");
            return enterStep(model, redirectModel);
        }
    }

    protected WirecardHopPaymentOperationsFacade getWirecardHopPaymentOperationsFacade() {
        return wirecardHopPaymentOperationsFacade;
    }

}
