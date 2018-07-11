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
package com.wirecard.hybris.core.payment.response.impl;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.strategy.PaymentOperationStrategy;
import com.wirecard.hybris.exception.WirecardNotEnrolledException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;

public class WirecardCreditCardCheckEnrollmentResponseHandler extends AbstractOrderCreditCardResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WirecardCreditCardCheckEnrollmentResponseHandler.class);

    private PaymentOperationStrategy paymentOperationStrategy;
    private Collection<String> checkEnrollmentErrors;

    @Override
    public void doOrderProcessResponse(final AbstractOrderModel abstractOrderModel, final PaymentOperationData data)
        throws WirecardPaymenException {

        if (LOG.isDebugEnabled()) {
            Payment response = data.getPayment();
            LOG.debug("Credit card is enrolled to 3ds (transaction id {})", response.getTransactionId());
        }
    }

    @Override
    protected void onException(AbstractOrderModel item, PaymentOperationData data, WirecardPaymenException exception)
        throws WirecardPaymenException {

        Payment response = data.getPayment();
        if (isNotEnrolled(response)) {
            data.setTokenId(response.getCardToken().getTokenId());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Credit card is not enrolled to 3ds (transaction id {})", response.getTransactionId(), exception);
            }
            throw new WirecardNotEnrolledException("Credit card is not enrolled to 3ds");
        } else {
            throw exception;
        }
    }

    protected boolean isNotEnrolled(Payment response) {
        return response.getStatuses().getStatus().stream()
                        .map(Status::getCode)
                        .anyMatch(code -> checkEnrollmentErrors.contains(code));
    }

    protected PaymentOperationStrategy getPaymentOperationStrategy() {
        return paymentOperationStrategy;
    }

    @Required
    public void setPaymentOperationStrategy(PaymentOperationStrategy paymentOperationStrategy) {
        this.paymentOperationStrategy = paymentOperationStrategy;
    }

    protected Collection<String> getCheckEnrollmentErrors() {
        return checkEnrollmentErrors;
    }

    @Required
    public void setCheckEnrollmentErrors(Collection<String> checkEnrollmentErrors) {
        this.checkEnrollmentErrors = checkEnrollmentErrors;
    }
}
