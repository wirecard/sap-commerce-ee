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

package com.wirecard.hybris.core.service.impl;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

public class DefaultWirecardPaymentService implements WirecardPaymentService {

    private ModelService modelService;

    private WirecardTransactionService wirecardTransactionService;

    @Override
    public ReturnStatus getReturnStatus(Payment response) {
        TransactionState transactionState;
        ReturnStatus returnStatus = ReturnStatus.PAYMENT_REVERSAL_FAILED;
        if (response != null) {
            transactionState = response.getTransactionState();
            if (transactionState == TransactionState.SUCCESS) {
                returnStatus = ReturnStatus.PAYMENT_REVERSED;
            }
        }
        return returnStatus;
    }

    public void setReturnRequestStatus(final ReturnRequestModel returnRequest, final ReturnStatus status) {
        returnRequest.setStatus(status);
        returnRequest.getReturnEntries().forEach(entry -> {
            entry.setStatus(status);
            getModelService().save(entry);
        });
        getModelService().save(returnRequest);
    }

    @Override
    public OrderStatus getOrderStatus(Payment response) {
        TransactionState transactionState;
        OrderStatus orderStatus = null;
        if (response != null) {
            transactionState = response.getTransactionState();
            if (response.getTransactionType().equals(TransactionType.AUTHORIZATION)) {

                if (transactionState == TransactionState.SUCCESS) {
                    orderStatus = OrderStatus.PAYMENT_AUTHORIZED;
                } else {
                    orderStatus = OrderStatus.PAYMENT_NOT_AUTHORIZED;
                }
            } else if (response.getTransactionType().equals(TransactionType.CAPTURE_AUTHORIZATION)
                || response.getTransactionType().equals(TransactionType.DEBIT)) {

                if (transactionState == TransactionState.SUCCESS) {
                    orderStatus = OrderStatus.PAYMENT_CAPTURED;
                } else {
                    orderStatus = OrderStatus.PAYMENT_NOT_CAPTURED;
                }
            }
        }
        return orderStatus;
    }

    @Override
    public void storePares(AbstractOrderModel abstractOrderModel, String pares) {
        PaymentTransactionModel transaction = wirecardTransactionService.lookForCompatibleTransactions(abstractOrderModel,
                                                                                                       abstractOrderModel.getPaymentMode()
                                                                                                                         .getCode());
        transaction.setPares(pares);
        modelService.save(transaction);

    }

    @Override
    public void storeToken(AbstractOrderModel abstractOrderModel, String token) {
        PaymentTransactionModel transaction = wirecardTransactionService.lookForCompatibleTransactions(abstractOrderModel,
                                                                                                       abstractOrderModel.getPaymentMode()
                                                                                                                         .getCode());
        transaction.setTokenId(token);
        // if a now token is set remove pares because it may be from previous transaction
        transaction.setPares(null);
        modelService.save(transaction);

    }

    @Override
    public void storeMPIthreeDparameters(AbstractOrderModel abstractOrderModel,
                                         String cardholderAuthenticationStatus,
                                         String cardholderAuthenticationValue,
                                         String eci,
                                         String xid) {
        PaymentTransactionModel transaction = wirecardTransactionService.lookForCompatibleTransactions(abstractOrderModel,
                                                                                                       abstractOrderModel.getPaymentMode()
                                                                                                                         .getCode());
        transaction.setCardholderAuthenticationStatus(cardholderAuthenticationStatus);
        transaction.setCardholderAuthenticationValue(cardholderAuthenticationValue);
        transaction.setEci(eci);
        transaction.setXid(xid);

        modelService.save(transaction);

    }

    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }

}
