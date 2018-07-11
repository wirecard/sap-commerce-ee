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

package com.wirecard.hybris.addon.controllers.pages.checkout;

import com.wirecard.hybris.core.constants.WirecardPaymentTransactionConstants;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.facades.WirecardCheckoutFacade;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.regex.Pattern;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/checkout/multi/wirecard/return")
public class WirecardReturnController {

    private static final Logger LOG = LoggerFactory.getLogger(WirecardReturnController.class);

    private static final String EPPRESPONSE_WAS = "eppresponse was: {}";
    private static final String PAYMENT_OPERATION_FAILED = "Payment operation failed.";
    private static final String CHECKOUT_PAYMENT_FAILED = "checkout.wirecard.payment.failed";
    private static final String WD = "wd-";
    private static final String REDIRECT_PREFIX = "redirect:";
    private static final String RESPONSE_ATTRIBUTES_INFO = "Response attributes for request {} was : trxid = {} , ec = {}";
    private static final String PROCESS_ASS_INFO = "Process acs response was called with pares: {}";
    private static final Pattern TRIM_NEW_LINE = Pattern.compile("[\r\n]");

    @Resource(name = "PAYPAL_RETURN_REDIRECT_ON_SUCCESS")
    private String paypalSuccessRedirectUrl;

    @Resource(name = "PAYPAL_RETURN_REDIRECT_ON_ERROR")
    private String paypalErrorRedirectUrl;

    @Resource(name = "MASTERPASS_RETURN_REDIRECT_ON_SUCCESS")
    private String masterpassSuccessRedirectUrl;

    @Resource(name = "MASTERPASS_RETURN_REDIRECT_ON_ERROR")
    private String masterpassErrorRedirectUrl;

    @Resource(name = "DEBIT_RETURN_REDIRECT_ON_SUCCESS")
    private String debitSuccessRedirectUrl;

    @Resource(name = "DEBIT_RETURN_REDIRECT_ON_ERROR")
    private String debitErrorRedirectUrl;

    @Resource(name = "INSTALLMENT_RETURN_REDIRECT_ON_SUCCESS")
    private String installmentSuccessRedirectUrl;

    @Resource(name = "INSTALLMENT_RETURN_REDIRECT_ON_ERROR")
    private String installmentErrorRedirectUrl;

    @Resource(name = "RATEPAY_INVOICE_RETURN_REDIRECT_ON_SUCCESS")
    private String ratepayInvoiceSuccessRedirectUrl;

    @Resource(name = "RATEPAY_INVOICE_RETURN_REDIRECT_ON_ERROR")
    private String ratepayInvoiceErrorRedirectUrl;

    @Resource(name = "wirecardCheckoutFacade")
    private WirecardCheckoutFacade wirecardCheckoutFacade;

    @Resource(name = "wirecardHopPaymentOperationsFacade")
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    @Resource(name = "wirecardPaymentConfigurationService")
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @RequestMapping(value = "/paypal", method = RequestMethod.POST)
    public String processReturn(@RequestParam(name = "eppresponse") String eppresponse, RedirectAttributes redirectAttrs) {

        LOG.info("Received paypal return call");
        return getReturnResultURL(eppresponse, redirectAttrs, paypalSuccessRedirectUrl, paypalErrorRedirectUrl,
                                  WirecardPaymentTransactionConstants.AUTHORIZATION_RETURN);
    }

    @RequestMapping(value = "/masterpass", method = RequestMethod.POST)
    public String processMasterpassReturn(@RequestParam(name = "eppresponse") String eppresponse, RedirectAttributes redirectAttrs) {

        LOG.info("Received masterpass return call");
        return getReturnResultURL(eppresponse, redirectAttrs, masterpassSuccessRedirectUrl,
                                  masterpassErrorRedirectUrl, WirecardPaymentTransactionConstants.AUTHORIZATION_RETURN);
    }

    @RequestMapping(value = "/debit", method = RequestMethod.POST)
    public String processDebitReturn(@RequestParam(name = "eppresponse") String eppresponse, RedirectAttributes redirectAttrs) {

        LOG.info("Received debit return call");
        return getReturnResultURL(eppresponse,
                                  redirectAttrs,
                                  debitSuccessRedirectUrl,
                                  debitErrorRedirectUrl,
                                  WirecardPaymentTransactionConstants.DEBIT_RETURN);
    }

    @RequestMapping(value = "/debit", method = RequestMethod.GET)
    public String processDebitIdealReturn(
        @RequestParam(name = "trxid", required = false) String trxid,
        @RequestParam(name = "ec", required = false) String ec,
        @RequestParam(name = "request_id", required = false) String requestId,
        RedirectAttributes redirectAttrs) {

        LOG.info("Received debit return call");
        return getReturnResultURL(trxid, ec, requestId, redirectAttrs,
                                  debitSuccessRedirectUrl, debitErrorRedirectUrl,
                                  WirecardPaymentTransactionConstants.DEBIT_RETURN);
    }

    private String getReturnResultURL(String trxid, String ec, String requestId,
                                      RedirectAttributes redirectAttrs,
                                      String onSuccessURL,
                                      String onErrorURL, String paymentOperationName) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(RESPONSE_ATTRIBUTES_INFO,
                      TRIM_NEW_LINE.matcher(requestId).replaceAll(""),
                      TRIM_NEW_LINE.matcher(trxid).replaceAll(""),
                      TRIM_NEW_LINE.matcher(ec).replaceAll(""));
        }

        if (StringUtils.isNoneEmpty(trxid, ec, requestId)) {

            String response = processFromIdealResponse(requestId);

            if (executePaymentOperation(paymentOperationName, response, false, false, redirectAttrs)) {
                return onSuccessURL;
            }
        }

        return onErrorURL;
    }

    private String getReturnResultURL(String eppresponse,
                                      RedirectAttributes redirectAttrs,
                                      String onSuccessURL,
                                      String onErrorURL, String paymentOperationName) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(EPPRESPONSE_WAS, TRIM_NEW_LINE.matcher(eppresponse).replaceAll(""));
        }

        if (executePaymentOperation(paymentOperationName, eppresponse, true, true, redirectAttrs)) {
            return onSuccessURL;
        }

        return onErrorURL;
    }

    private boolean executePaymentOperation(String paymentOperationName, String response, boolean isEncoded, boolean checkSignature,
                                            RedirectAttributes redirectAttrs) {
        if (StringUtils.isNotEmpty(response)) {
            try {

                Payment payment = wirecardHopPaymentOperationsFacade.parseMessage(response, isEncoded, checkSignature);
                PaymentOperationData data = new PaymentOperationData();
                data.setPayment(payment);

                wirecardHopPaymentOperationsFacade.executePaymentOperation(paymentOperationName, data);
                return true;

            } catch (WirecardPaymenException e) {
                LOG.error(PAYMENT_OPERATION_FAILED, e);
            }
        }
        GlobalMessages.addFlashMessage(redirectAttrs, GlobalMessages.ERROR_MESSAGES_HOLDER, CHECKOUT_PAYMENT_FAILED);
        return false;
    }

    private String processFromIdealResponse(String requestId) {

        return wirecardHopPaymentOperationsFacade.getPaymentResponseFromWirecard(WD + PaymentMethodName.IDEAL.value(), requestId);

    }

    @RequestMapping(value = "/ratepayinstall", method = RequestMethod.POST)
    public String processInstallmentReturn(@RequestParam(name = "base64payload") String base64payload, RedirectAttributes redirectAttrs) {

        LOG.info("Received installment return call");
        return getReturnResultURL(base64payload, redirectAttrs, installmentSuccessRedirectUrl,
                                  installmentErrorRedirectUrl, WirecardPaymentTransactionConstants.AUTHORIZATION_RETURN);
    }

    @RequestMapping(value = "/ratepayinvoice", method = RequestMethod.POST)
    public String processRatepayInvoiceReturn(@RequestParam(name = "base64payload") String base64payload,
                                              RedirectAttributes redirectAttrs) {

        LOG.info("Received ratepay invoice return call");
        return getReturnResultURL(base64payload, redirectAttrs, ratepayInvoiceSuccessRedirectUrl,
                                  ratepayInvoiceErrorRedirectUrl, WirecardPaymentTransactionConstants.AUTHORIZATION_RETURN);
    }

    @RequestMapping(value = {"/{paymentMethod}/pares"}, method = RequestMethod.POST)
    public String processAcsResponse(@PathVariable(value = "paymentMethod") final String paymentMethod,
                                     @RequestParam(value = "PaRes", required = false) final String pares,
                                     Model model, RedirectAttributes redirectAttributes) {

        if (LOG.isInfoEnabled()) {
            LOG.info(PROCESS_ASS_INFO, TRIM_NEW_LINE.matcher(pares).replaceAll(""));
        }

        PaymentOperationData data = new PaymentOperationData();

        data.setPares(pares);
        wirecardCheckoutFacade.setAuthentication(data);

        try {
            wirecardHopPaymentOperationsFacade.executePaymentOperation(WirecardPaymentTransactionConstants.AUTHORIZATION3D, data);
            wirecardCheckoutFacade.storePares(pares);
        } catch (WirecardPaymenException e) {
            LOG.error("Payment failed", e);

            GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER, e.getMessage());

            return REDIRECT_PREFIX + wirecardPaymentConfigurationService.getCancelURL(null);
        }

        return REDIRECT_PREFIX + wirecardPaymentConfigurationService.getSuccesURL(null);
    }

}
