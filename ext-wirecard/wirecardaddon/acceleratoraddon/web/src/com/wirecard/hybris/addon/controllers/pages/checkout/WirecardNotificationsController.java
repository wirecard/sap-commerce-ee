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
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/checkout/multi/wirecard")
public class WirecardNotificationsController {

    private static final String START_MESSAGE = "Received notification call";
    private static final String NOTIFICATION_MESSAGE = "Notification body was: {}";

    private static final Logger LOG = LoggerFactory.getLogger(WirecardNotificationsController.class);

    private static final Pattern TRIM_NEW_LINE = Pattern.compile("[\r\n]");

    @Autowired
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    @RequestMapping(value = "/paymentnotifications", method = RequestMethod.POST)
    public void processNotification(@RequestBody String content, HttpServletResponse httpServletResponse) {

        LOG.info(START_MESSAGE);
        if (LOG.isDebugEnabled()) {
            LOG.debug(NOTIFICATION_MESSAGE, TRIM_NEW_LINE.matcher(content).replaceAll(""));
        }

        try {

            if (content != null) {

                Payment payment = wirecardHopPaymentOperationsFacade.parseMessage(content, true, true);

                PaymentOperationData data = new PaymentOperationData();
                data.setPayment(payment);

                wirecardHopPaymentOperationsFacade.executePaymentOperation(WirecardPaymentTransactionConstants.NOTIFICATION,
                                                                           data, payment.getOrderNumber());

                httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            }
        } catch (WirecardPaymenException ex) {
            httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            LOG.error("Payment operation failed.", ex);
        }
    }

}
