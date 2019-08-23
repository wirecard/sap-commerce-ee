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

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.operation.PaymentOperation;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.operation.PaymentProcessor;
import com.wirecard.hybris.core.payment.command.PaymentCommand;
import com.wirecard.hybris.core.payment.response.ResponseHandler;
import com.wirecard.hybris.core.payment.transaction.WirecardTransactionData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.core.strategy.TransactionTypeStrategy;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

public class DefaultPaymentOperation<T extends ItemModel> implements PaymentOperation<T> {

    private Converter<T, Payment> paymentDataConverter;

    private PaymentCommand paymentCommand;

    private ResponseHandler<T> responseHandler;

    private TransactionTypeStrategy transactionTypeStrategy;

    private PaymentProcessor paymentProcessor;

    private WirecardTransactionService wirecardTransactionService;

    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    private boolean isFallBack;

    private boolean isParentOperation;

    @Override
    public Payment doOperation(T operationData, PaymentOperationData data) throws WirecardPaymenException {
        WirecardTransactionData wirecardTransactionData = new WirecardTransactionData();
        try {
            // fill all the necessary data for logging the transaction in finally block
            AbstractOrderModel orderModel = getResponseHandler().getOrder(operationData);
            wirecardTransactionData.setAbstractOrderModel(orderModel);
            wirecardTransactionData.setCheckoutPaymentType(orderModel.getPaymentMode().getCode());
            wirecardTransactionData.setTransactionAmount(getResponseHandler().getTransactionAmount(operationData));

            // Initially set the payment transaction type for error cases later on we will call it again
            PaymentTransactionType paymentTransactionType = getTransactionTypeStrategy().getPaymentTransactionType(null);
            wirecardTransactionData.setPaymentTransactionType(paymentTransactionType);

            if (data == null) {
                data = new PaymentOperationData();
            }

            WirecardAuthenticationModel authenticationModel = getAuthentication(orderModel, data);
            wirecardTransactionData.setAuthenticationModel(authenticationModel);

            // create the transaction to get authentication and maid from in data converter
            wirecardTransactionService.createTransaction(wirecardTransactionData);

            // payment data from converter has precedence over passed data
            Payment payment = data.getPayment();

            if (getPaymentDataConverter() != null) {
                payment = getPaymentDataConverter().convert(operationData);
            }

            if (payment == null) {
                throw new WirecardPaymenException(
                    "Could not execute payment operation neither a data converter nor payment in passed data object are available");
            }

            if(data.isSavedCC())
            {
                String challengeMandate = "04";
                if(payment.getThreeDSRequestor()!=null)
                {
                    payment.getThreeDSRequestor().setThreeDSRequestorChallengeInd(challengeMandate);
                }
            }

            if (getPaymentProcessor() != null) {
                getPaymentProcessor().processPayment(payment, data);
            }

            wirecardTransactionData.setRequest(payment);
            Payment response = getPaymentCommand().execute(payment, authenticationModel);
            data.setPayment(response);
            wirecardTransactionData.setResponse(response);

            String transactionId;
            if (isParentOperation) {
                transactionId = response.getParentTransactionId();
            } else {
                transactionId = response.getTransactionId();
            }

            wirecardTransactionData.setTransactionId(transactionId);

            // update the payment transaction type because we have now all information which may be needed to resolve it
            // (e.g. used for notification)
            paymentTransactionType = getTransactionTypeStrategy().getPaymentTransactionType(wirecardTransactionData);
            wirecardTransactionData.setPaymentTransactionType(paymentTransactionType);

            getResponseHandler().processResponse(operationData, data);
            return response;
        } catch (RuntimeException e) {
            throw new WirecardPaymenException("Payment operation failed: " + e.getMessage(), e);
        } finally {
            // Create the PaymentTransaction and PaymentTransactionEntry
            wirecardTransactionService.logTransactionData(wirecardTransactionData);
        }
    }

    private WirecardAuthenticationModel getAuthentication(AbstractOrderModel order, PaymentOperationData data)
        throws WirecardPaymenException {
        WirecardAuthenticationModel authenticationModel;
        if (isFallBack()) {
            authenticationModel = data.getWirecardFallbackAuthenticationModel();
            if (authenticationModel == null) {
                authenticationModel = data.getWirecardAuthenticationModel();
            }
        } else {
            authenticationModel = wirecardPaymentConfigurationService.getAuthentication(order);
            if (authenticationModel == null) {
                authenticationModel = data.getWirecardAuthenticationModel();
            }
        }

        if (authenticationModel != null) {
            return authenticationModel;
        } else {
            throw new WirecardPaymenException("Authentication can't be null");
        }
    }

    protected PaymentCommand getPaymentCommand() {
        return paymentCommand;
    }

    @Required
    public void setPaymentCommand(PaymentCommand paymentCommand) {
        this.paymentCommand = paymentCommand;
    }

    protected Converter<T, Payment> getPaymentDataConverter() {

        return paymentDataConverter;
    }

    public void setPaymentDataConverter(Converter<T, Payment> paymentDataConverter) {
        this.paymentDataConverter = paymentDataConverter;
    }

    protected ResponseHandler<T> getResponseHandler() {
        return responseHandler;
    }

    @Required
    public void setResponseHandler(ResponseHandler<T> responseHandler) {
        this.responseHandler = responseHandler;
    }

    protected TransactionTypeStrategy getTransactionTypeStrategy() {
        return transactionTypeStrategy;
    }

    @Required
    public void setTransactionTypeStrategy(TransactionTypeStrategy transactionTypeStrategy) {
        this.transactionTypeStrategy = transactionTypeStrategy;
    }

    protected PaymentProcessor getPaymentProcessor() {
        return paymentProcessor;
    }

    public void setPaymentProcessor(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }

    protected boolean isFallBack() {
        return isFallBack;
    }

    @Required
    public void setFallBack(boolean fallBack) {
        isFallBack = fallBack;
    }

    public WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }

    public boolean isParentOperation() {
        return isParentOperation;
    }

    public void setParentOperation(boolean parentOperation) {
        isParentOperation = parentOperation;
    }
}
