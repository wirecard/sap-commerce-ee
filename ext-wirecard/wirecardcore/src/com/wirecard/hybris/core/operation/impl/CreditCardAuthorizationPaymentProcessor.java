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

package com.wirecard.hybris.core.operation.impl;

import com.wirecard.hybris.core.data.types.Card;
import com.wirecard.hybris.core.data.types.CardToken;
import com.wirecard.hybris.core.data.types.ObjectFactory;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.Periodic;
import com.wirecard.hybris.core.data.types.PeriodicType;
import com.wirecard.hybris.core.data.types.SequenceType;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.operation.PaymentProcessor;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import org.springframework.beans.factory.annotation.Required;

public class CreditCardAuthorizationPaymentProcessor implements PaymentProcessor {

    private ObjectFactory objectFactory;
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    @Override
    public void processPayment(Payment payment, PaymentOperationData data) throws WirecardPaymenException {
        String token = data.getTokenId();
        CardToken cardToken = new CardToken();
        cardToken.setTokenId(token);
        payment.setCardToken(cardToken);

        TransactionType transactionType = payment.getTransactionType();

        if ((TransactionType.AUTHORIZATION).equals(transactionType)) {
            checkRecurringPayment(payment, data);
        } else if ((TransactionType.CHECK_ENROLLMENT).equals(transactionType)) {
            setCardSavedStatusInSession(data);
        }
    }

    private void setCardSavedStatusInSession(PaymentOperationData data) {
        if (data.isSaveInAccount()) {
            wirecardHopPaymentOperationsFacade.setIsSaveInAccount(true);

        } else if (data.isSavedCC()) {
            wirecardHopPaymentOperationsFacade.setIsSavedCC(true);
        }

    }

    private void checkRecurringPayment(Payment payment, PaymentOperationData data) {
        if (data.isSaveInAccount()) {
            setPeriodic(payment, SequenceType.FIRST);
            setCardTokenizationFlag(payment);

        } else if (data.isSavedCC()) {
            setPeriodic(payment, SequenceType.RECURRING);
            setCardTokenizationFlag(payment);
        }
    }

    private void setPeriodic(Payment payment, SequenceType sequenceType) {
        Periodic periodic = objectFactory.createPeriodic();
        periodic.setPeriodicType(PeriodicType.CI);
        periodic.setSequenceType(sequenceType);
        payment.setPeriodic(periodic);
    }

    private void setCardTokenizationFlag(Payment payment) {
        Card card = objectFactory.createCard();
        card.setMerchantTokenizationFlag(true);
        payment.setCard(card);
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    protected WirecardHopPaymentOperationsFacade getWirecardHopPaymentOperationsFacade() {
        return wirecardHopPaymentOperationsFacade;
    }

    @Required
    public void setWirecardHopPaymentOperationsFacade(WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade) {
        this.wirecardHopPaymentOperationsFacade = wirecardHopPaymentOperationsFacade;
    }
}
