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
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.WirecardPaymentInfoModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWirecardNotificationResponseHandler<T extends WirecardPaymentInfoModel> extends DefaultOrderResponseHandler<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWirecardNotificationResponseHandler.class);

    @Override
    protected boolean shouldProcessResponse(AbstractOrderModel item, PaymentOperationData data) {
        boolean inFinalState = isInFinalState(item);
        if (inFinalState) {
            LOG.info("Detected final state {} on order. Will not change status anymore", item.getPaymentStatus());
        }
        return !inFinalState;
    }

    @Override
    public void doOrderProcessResponse(AbstractOrderModel abstractOrder, PaymentOperationData data) throws WirecardPaymenException {
        LOG.info("Notification is being processed for order: {} ({})", abstractOrder.getCode(), abstractOrder.getGuid());
        updatePaymentStatus(abstractOrder, data.getPayment());
    }

    /**
     * Checks whether an order is in a final state or not.
     * The final state is defined as a payment status {@link PaymentStatus#ERROR} or {@link PaymentStatus#PAID}
     *
     * @param abstractOrder
     *     the order to be checked
     * @return true if in final state else false
     */
    private boolean isInFinalState(AbstractOrderModel abstractOrder) {
        PaymentStatus currentStatus = abstractOrder.getPaymentStatus();
        return PaymentStatus.ERROR.equals(currentStatus) || PaymentStatus.PAID.equals(currentStatus);
    }

    /**
     * set payment status to paid and save it if there is a success capture
     *
     * @param abstractOrder
     *     the order
     * @param response
     *     the recived notification
     */
    private void updatePaymentStatus(AbstractOrderModel abstractOrder, Payment response) {

        if ((isCaptureAuthorizationOrDebit(response) || isWiretranferAuthorization(abstractOrder, response))
            && TransactionState.SUCCESS.equals(response.getTransactionState())) {
            storePaymentStatus(abstractOrder, PaymentStatus.PAID);
        }
    }

    private boolean isWiretranferAuthorization(AbstractOrderModel abstractOrder, Payment response) {
        return PaymentMethodName.WIRETRANSFER.value().equals(abstractOrder.getPaymentMode().getPaymentAlias())
            && TransactionType.AUTHORIZATION.equals(response.getTransactionType());
    }

    private boolean isCaptureAuthorizationOrDebit(Payment response) {
        return TransactionType.CAPTURE_AUTHORIZATION.equals(response.getTransactionType())
            || TransactionType.DEBIT.equals(response.getTransactionType());
    }

    private void storePaymentStatus(AbstractOrderModel abstractOrder, PaymentStatus paymentStatus) {
        abstractOrder.setPaymentStatus(paymentStatus);
        getModelService().save(abstractOrder);
    }
}
