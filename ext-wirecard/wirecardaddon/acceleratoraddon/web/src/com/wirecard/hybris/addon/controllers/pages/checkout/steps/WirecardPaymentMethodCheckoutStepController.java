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
import com.wirecard.hybris.addon.forms.WirecardPaymentDetailsForm;
import com.wirecard.hybris.addon.forms.validation.WirecardPaymentDetailsValidator;
import com.wirecard.hybris.core.converter.data.PaymentModeData;
import com.wirecard.hybris.core.data.SepaMandateData;
import com.wirecard.hybris.core.data.WirecardPaymentMethodParameters;
import com.wirecard.hybris.core.data.WirecardRequestData;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethod;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.exception.WirecardNotEnrolledException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import com.wirecard.hybris.facades.WirecardPaymentModeFacade;
import com.wirecard.hybris.facades.impl.DefaultWirecardCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.acceleratorstorefrontcommons.util.AddressDataUtil;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "/checkout/multi/wirecard/payment-method")
public class WirecardPaymentMethodCheckoutStepController extends AbstractCheckoutStepController {

    private static final Logger LOGGER = Logger.getLogger(WirecardPaymentMethodCheckoutStepController.class);
    private static final String PAYMENT_METHOD = "payment-method";
    private static final String CART_DATA_ATTR = "cartData";
    private static final String BILLING_COUNTRIES = "billingCountries";
    private static final String NOTPROVIDED = "checkout.multi.paymentDetails.notprovided";
    private static final String GENERAL_ERROR = "checkout.multi.paymentMethod.addPaymentDetails.generalError";
    private static final String INVALID = "checkout.error.paymentethod.formentry.invalid";
    private static final String NOT_ACTIVE = "checkout.wirecard.payment.not.active";
    private static final String PAYMENT_METHOD_SELECTED_LOG_MSG = "request for payment of goods through %s payment method";
    private static final String REDIRECT_LOG_MSG = "redirecting to %s";
    private static final String PURCHASE_METHOD_SELECTED_LOG_MSG = "payment method %s is purchase. Redirecting to summary";
    private static final String HOP_AUTHORIZATION_OPERATION_LOG_MSG = "executing Authorizacion payment operation with HOP";
    private static final String AUTHORIZATION_OPERATION_LOG_MSG = "executing Authorizacion payment operation without HOP";
    private static final String CANCEL_MSG = "wirecard.msg.cancel";
    private static final String CREDIT_CARD = "creditcard";
    private static final String WD_PAYPAL = "wd-paypal";
    private static final String WD_MASTERPASS = "wd-masterpass";
    private static final String WD_POIPIA = "wd-poipia";
    private static final String WD_CREDITCARD = "wd-creditcard";
    private static final String WD_WIRECARD_INSTALL = "wd-wirecard-install";
    private static final String WD_WIRECARD_INVOICE = "wd-wirecard-invoice";
    private static final String WD_SEPA = "wd-sepa";
    private static final String WD_IDEAL = "wd-ideal";
    private static final String WD_UNIONPAY = "wd-unionpayinternational";
    private static final Set<String> WDS_COMPARE_TO_REDIRECT = new HashSet<>(Arrays.asList(WD_PAYPAL, WD_MASTERPASS, WD_WIRECARD_INSTALL));
    private static final Set<String> WDS_COMPARE_TO_SUMMARY =
        new HashSet<>(Arrays.asList(WD_POIPIA, WD_SEPA, WD_WIRECARD_INVOICE));

    @Resource(name = "addressDataUtil")
    private AddressDataUtil addressDataUtil;

    @Resource(name = "wirecardPaymentModeFacade")
    private WirecardPaymentModeFacade wirecardPaymentModeFacade;

    @Resource(name = "wirecardHopPaymentOperationsFacade")
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    @Resource(name = "wirecardPaymentDetailsValidator")
    private WirecardPaymentDetailsValidator wirecardPaymentDetailsValidator;

    @Resource(name = "checkoutFacade")
    private DefaultWirecardCheckoutFacade wirecardCheckoutFacade;

    @Resource(name = "wirecardPaymentConfigurationService")
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Override
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @RequireHardLogIn
    @PreValidateQuoteCheckoutStep
    @PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
    public String enterStep(final Model model,
                            final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {

        getCheckoutFacade().setDeliveryModeIfAvailable();

        final CartData cartData = getCheckoutFacade().getCheckoutCart();

        setupAddPaymentPage(model);
        setupActivePaymentMethods(model);

        setCheckoutStepLinksForModel(model, getCheckoutStep());

        AddressForm addressForm = getAddressForm(cartData, model);

        final WirecardPaymentDetailsForm wirecardPaymentDetailsForm = new WirecardPaymentDetailsForm();
        wirecardPaymentDetailsForm.setBillingAddress(addressForm);
        setupWirecardPaymentPostPage(wirecardPaymentDetailsForm, model);
        model.addAttribute(CART_DATA_ATTR, cartData);
        model.addAttribute(BILLING_COUNTRIES, getCheckoutFacade().getBillingCountries());

        return WirecardaddonControllerConstants.wirecardPaymentMethodPage;
    }


    private void setupActivePaymentMethods(final Model model) {

        final List<PaymentModeData> activePaymentMethods = wirecardPaymentModeFacade.getActivePaymentModes();
        model.addAttribute("paymentMethodList", activePaymentMethods);

        wirecardHopPaymentOperationsFacade.setIsSavedCC(false);
        wirecardHopPaymentOperationsFacade.setIsSaveInAccount(false);

        PaymentModeData paymentModeData = wirecardPaymentModeFacade.getActiveCreditCardPaymentMode(activePaymentMethods, CREDIT_CARD);

        if (paymentModeData != null) {

            WirecardRequestData wirecardRequestData = wirecardCheckoutFacade.getWireCardRequestData(WD_CREDITCARD);
            model.addAttribute("wirecardRequestData", wirecardRequestData);
            model.addAttribute("wirecardURL", wirecardCheckoutFacade.getWirecardBaseURL(paymentModeData.getCode()));
            model.addAttribute("creditCardCode", paymentModeData.getCode());
        }


        /*
       Data for SEPA Mandate pop up
        */
        if (wirecardPaymentModeFacade.isPaymentMethodActive(WD_SEPA)) {
            SepaMandateData sepaMandateData = wirecardPaymentModeFacade.getSepaMandateData(WD_SEPA);
            model.addAttribute("sepaMandateData", sepaMandateData);
        }
    }

    @RequestMapping(value = {"/add"}, method = RequestMethod.POST)
    @RequireHardLogIn
    public String add(final Model model,
                      final HttpServletRequest request,
                      @Valid final WirecardPaymentDetailsForm wirecardPaymentDetailsForm,
                      final BindingResult bindingResult, RedirectAttributes redirectAttributes)
        throws CMSItemNotFoundException {

        WirecardPaymentMethodParameters checkoutStepParametersData = getParameters(request);
        String paymentMethodChosen = checkoutStepParametersData.getPaymentMethodChosen();

        LOGGER.info(String.format(PAYMENT_METHOD_SELECTED_LOG_MSG, paymentMethodChosen));

        if (paymentMethodChosen == null) {
            GlobalMessages.addErrorMessage(model, NOTPROVIDED);
            return enterStep(model, redirectAttributes);
        }

        if (wirecardPaymentModeFacade.isPaymentMethodChooseInactive(paymentMethodChosen)) {

            GlobalMessages.addErrorMessage(model, NOT_ACTIVE);
            return enterStep(model, redirectAttributes);
        }

        getWirecardPaymentDetailsValidator().validate(wirecardPaymentDetailsForm, bindingResult);
        if (bindingResult.hasErrors()) {
            GlobalMessages.addErrorMessage(model, INVALID);
            return enterStep(model, redirectAttributes);
        }

        Boolean isNewBillingAddress = wirecardPaymentDetailsForm.isNewBillingAddress();
        if (BooleanUtils.isTrue(isNewBillingAddress)) {
            final AddressData addressData = getAddressData(wirecardPaymentDetailsForm);

            getAddressVerificationFacade().verifyAddressData(addressData);
            getCheckoutFacade().setPaymentAddress(addressData);
        } else {
            getCheckoutFacade().setSamePaymentAdress();
        }

        getCheckoutFacade().setPaymentMode(paymentMethodChosen);

        if (wirecardPaymentModeFacade.isPurchase(paymentMethodChosen)) {
            LOGGER.info(String.format(PURCHASE_METHOD_SELECTED_LOG_MSG, paymentMethodChosen));

            getWirecardHopPaymentOperationsFacade().setIdealBic(checkoutStepParametersData.getBic());
            return getCheckoutStep().nextStep();
        }
        try {

            PaymentOperationData data = new PaymentOperationData();
            data.setTokenId(checkoutStepParametersData.getTokenId());
            if (StringUtils.isNotEmpty(checkoutStepParametersData.getIsSavedCC())) {
                data.setSavedCC(true);
            }
            data.setSaveInAccount(wirecardPaymentDetailsForm.isSaveInAccount());
            data.setBic(checkoutStepParametersData.getBic());
            data.setIban(checkoutStepParametersData.getIban());
            data.setSepaAccountOwner(checkoutStepParametersData.getBankAccountOwner());

            getCheckoutFacade().setAuthentication(data);
            Payment payment = getWirecardHopPaymentOperationsFacade().executePaymentOperation(data);
            if (payment == null) {
                GlobalMessages.addErrorMessage(model, GENERAL_ERROR);
                return enterStep(model, redirectAttributes);
            }

            return preparePageForOperationResponse(model, payment, paymentMethodChosen, redirectAttributes);

        } catch (WirecardNotEnrolledException e) {
            LOGGER.error("Wirecard not enrolled", e);
            GlobalMessages.addErrorMessage(model, "checkout.wirecard.payment.notenrolled");
            return enterStep(model, redirectAttributes);
        } catch (WirecardPaymenException e) {
            LOGGER.error("Payment failed", e);

            String message;
            if (StringUtils.isBlank(e.getMessage())) {
                message = "checkout.wirecard.payment.failed";
            } else {
                message = e.getMessage();
            }
            GlobalMessages.addErrorMessage(model, message);
            return enterStep(model, redirectAttributes);
        }
    }

    private WirecardPaymentMethodParameters getParameters(HttpServletRequest request) {
        WirecardPaymentMethodParameters checkoutStepParametersData = new WirecardPaymentMethodParameters();
        checkoutStepParametersData.setPaymentMethodChosen(request.getParameter("paymentMethodChosen"));
        checkoutStepParametersData.setTokenId(request.getParameter("tokenId"));
        checkoutStepParametersData.setIsSavedCC(request.getParameter("isSavedCC"));

        checkoutStepParametersData.setBankAccountOwner(request.getParameter("bankAccountOwner"));

        String iban = request.getParameter("bankAccountIban");

        if (StringUtils.isNotEmpty(iban)) {
            checkoutStepParametersData.setIban(iban);
        }

        String bic = StringUtils.EMPTY;
        if (WD_SEPA.equals(checkoutStepParametersData.getPaymentMethodChosen())) {
            bic = request.getParameter("bankBic");
        } else if (WD_IDEAL.equals(checkoutStepParametersData.getPaymentMethodChosen())){
            bic = request.getParameter("ideal-bic");
        }

        if (StringUtils.isNotEmpty(bic)) {
            checkoutStepParametersData.setBic(bic);
        }

        return checkoutStepParametersData;
    }

    private AddressData getAddressData(WirecardPaymentDetailsForm wirecardPaymentDetailsForm) {

        final AddressForm addressForm = wirecardPaymentDetailsForm.getBillingAddress();
        AddressData addressData = addressDataUtil.convertToAddressData(addressForm);
        addressData.setShippingAddress(BooleanUtils.isTrue(addressForm.getShippingAddress()));
        addressData.setBillingAddress(BooleanUtils.isTrue(addressForm.getBillingAddress()));

        return addressData;
    }

    private String preparePageForOperationResponse(Model model,
                                                   Payment payment,
                                                   String paymentMethodChosen,
                                                   RedirectAttributes redirectAttributes)
        throws CMSItemNotFoundException {
        String responsePage = null;

        if (TransactionState.SUCCESS.value().equals(payment.getTransactionState().value())) {
            if (payment.getThreeD() == null) {
                if (WDS_COMPARE_TO_REDIRECT.contains(paymentMethodChosen)) {
                    LOGGER.info(HOP_AUTHORIZATION_OPERATION_LOG_MSG);

                    responsePage = payment.getPaymentMethods().getPaymentMethod().stream()
                                          .map(PaymentMethod::getUrl)
                                          .findAny()
                                          .map(url -> REDIRECT_PREFIX + url)
                                          .orElse(REDIRECT_URL_ERROR);

                } else if (paymentMethodChosen.startsWith(WD_CREDITCARD)
                    || WDS_COMPARE_TO_SUMMARY.contains(paymentMethodChosen)) {
                    LOGGER.info(AUTHORIZATION_OPERATION_LOG_MSG);
                    responsePage = REDIRECT_PREFIX + wirecardPaymentConfigurationService.getSuccesURL(null);
                }
            } else {
                LOGGER.info(HOP_AUTHORIZATION_OPERATION_LOG_MSG);
                String paReq = (String) payment.getThreeD().getContent().get(0).getValue();
                String acsUrl = (String) payment.getThreeD().getContent().get(1).getValue();
                String termUrl = String.format(wirecardPaymentConfigurationService.getAcsTermURL(), paymentMethodChosen);

                model.addAttribute("paReq", paReq);
                model.addAttribute("acsUrl", acsUrl);
                model.addAttribute("termUrl", termUrl);
                setupAddPaymentPage(model);

                responsePage = WirecardaddonControllerConstants.threeDpostPage;
            }
        } else {
            GlobalMessages.addErrorMessage(model, payment.getStatuses().getStatus().stream()
                                                         .map(Status::getDescription)
                                                         .findAny()
                                                         .orElse(GENERAL_ERROR));
            return enterStep(model, redirectAttributes);
        }

        LOGGER.info(String.format(REDIRECT_LOG_MSG, responsePage));

        return responsePage;
    }

    @RequestMapping(value = {"/cancel"}, method = RequestMethod.GET)
    public String cancel(Model model, RedirectAttributes redirectAttributes)
        throws CMSItemNotFoundException {

        if (!model.containsAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER)) {
            GlobalMessages.addErrorMessage(model, CANCEL_MSG);
        }

        return enterStep(model, redirectAttributes);
    }

    private boolean existBillingAddressInCartData(final CartData cartData) {
        return cartData.getPaymentInfo() != null && cartData.getPaymentInfo().getBillingAddress() != null;
    }

    private void setupWirecardPaymentPostPage(final WirecardPaymentDetailsForm wirecardPaymentDetailsForm, final Model model) {

        final CartData cartData = getCheckoutFacade().getCheckoutCart();
        model.addAttribute("commonPaymentDetailsForm", new WirecardPaymentDetailsForm());
        model.addAttribute(CART_DATA_ATTR, cartData);
        model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
        model.addAttribute("wirecardPaymentDetailsForm", wirecardPaymentDetailsForm);
        model.addAttribute("paymentInfos", getCheckoutFacade().getWirecardPaymentInfos(true));
        if (StringUtils.isNotBlank(wirecardPaymentDetailsForm.getBillToCountry())) {
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(wirecardPaymentDetailsForm.getBillToCountry()));
            model.addAttribute("country", wirecardPaymentDetailsForm.getBillToCountry());
        }
    }

    private AddressForm populateAddressForm(final AddressData addressData) {
        final AddressForm addressForm = new AddressForm();
        addressForm.setAddressId(addressData.getId());
        addressForm.setFirstName(addressData.getFirstName());
        addressForm.setLastName(addressData.getLastName());
        addressForm.setLine1(addressData.getLine1());
        if (addressData.getLine2() != null) {
            addressForm.setLine2(addressData.getLine2());
        }
        addressForm.setTownCity(addressData.getTown());
        addressForm.setPostcode(addressData.getPostalCode());
        addressForm.setCountryIso(addressData.getCountry().getIsocode());
        if (addressData.getRegion() != null) {
            addressForm.setRegionIso(addressData.getRegion().getIsocode());
        }
        addressForm.setShippingAddress(addressData.isShippingAddress());
        addressForm.setBillingAddress(addressData.isBillingAddress());
        if (addressData.getPhone() != null) {
            addressForm.setPhone(addressData.getPhone());
        }
        return addressForm;

    }


    private AddressForm getAddressForm(CartData cartData, Model model) {
        AddressForm addressForm = new AddressForm();
        if (existBillingAddressInCartData(cartData)) {
            addressForm = populateAddressForm(cartData.getPaymentInfo().getBillingAddress());
        } else if (cartData.getDeliveryAddress() != null) {
            addressForm = populateAddressForm(cartData.getDeliveryAddress());
        }

        if (StringUtils.isNotBlank(addressForm.getCountryIso())) {
            model.addAttribute("regions", getI18NFacade().getRegionsForCountryIso(addressForm.getCountryIso()));
            model.addAttribute("country", addressForm.getCountryIso());
        }
        return addressForm;
    }

    @RequestMapping(value = "/back", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String back(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().previousStep();
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    @RequireHardLogIn
    @Override
    public String next(final RedirectAttributes redirectAttributes) {
        return getCheckoutStep().nextStep();
    }

    protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException {
        model.addAttribute("metaRobots", "noindex,nofollow");
        model.addAttribute("hasNoPaymentInfo", getCheckoutFlowFacade().hasNoPaymentInfo());
        prepareDataForPage(model);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                           getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
        final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        setCheckoutStepLinksForModel(model, getCheckoutStep());
    }

    protected CheckoutStep getCheckoutStep() {
        return getCheckoutStep(PAYMENT_METHOD);
    }

    protected WirecardHopPaymentOperationsFacade getWirecardHopPaymentOperationsFacade() {
        return wirecardHopPaymentOperationsFacade;
    }

    @Override
    protected DefaultWirecardCheckoutFacade getCheckoutFacade() {
        return (DefaultWirecardCheckoutFacade) super.getCheckoutFacade();
    }

    protected WirecardPaymentDetailsValidator getWirecardPaymentDetailsValidator() {
        return wirecardPaymentDetailsValidator;
    }
}
