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

package com.wirecard.hybris.core.strategy.impl;

import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.payment.transaction.WirecardTransactionData;
import com.wirecard.hybris.core.strategy.TransactionTypeStrategy;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

public class NotificationTransactionTypeStrategy implements TransactionTypeStrategy {
    private Map<TransactionType, PaymentTransactionType> paymentTransactionMapping;

    @Override
    public PaymentTransactionType getPaymentTransactionType(WirecardTransactionData wirecardTransactionData) {
        PaymentTransactionType paymentTransactionType = PaymentTransactionType.NOTIFICATION;
        if (wirecardTransactionData != null && wirecardTransactionData.getResponse() != null
            && getPaymentTransactionMapping().containsKey(wirecardTransactionData.getResponse().getTransactionType())) {
            paymentTransactionType = getPaymentTransactionMapping().get(wirecardTransactionData.getResponse().getTransactionType());
        }
        return paymentTransactionType;
    }

    protected Map<TransactionType, PaymentTransactionType> getPaymentTransactionMapping() {
        return paymentTransactionMapping;
    }

    @Required
    public void setPaymentTransactionMapping(Map<TransactionType, PaymentTransactionType> paymentTransactionMapping) {
        this.paymentTransactionMapping = paymentTransactionMapping;
    }
}
