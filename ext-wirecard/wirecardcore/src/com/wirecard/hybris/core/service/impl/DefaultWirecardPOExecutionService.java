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

import com.wirecard.hybris.core.constants.WirecardPaymentTransactionConstants;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.WirecardPOExecutionService;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardPaymentModeService;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.core.strategy.PaymentOperationStrategy;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class DefaultWirecardPOExecutionService implements WirecardPOExecutionService {

    private static final Logger LOGGER = Logger.getLogger(DefaultWirecardPOExecutionService.class);

    private PaymentOperationStrategy paymentOperationStrategy;
    private WirecardPaymentService wirecardPaymentService;
    private WirecardPaymentModeService wirecardPaymentModeService;
    private WirecardTransactionService wirecardTransactionService;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Override
    public OrderStatus executePaymentCaptureOperation(AbstractOrderModel abstractOrderModel, PaymentOperationData data) {
        Payment response = null;
        //if we have an authorization transaction we can capture
        if (wirecardTransactionService.lookForAcceptedTransactions(abstractOrderModel, PaymentTransactionType.AUTHORIZATION)) {
            // For Payment in advance(PIA) and payment on invoice(POI) there isn't transaction for Capture, we will only authorize.
            if (PaymentMethodName.WIRETRANSFER.value().equals(abstractOrderModel.getPaymentMode().getPaymentAlias())) {
                return OrderStatus.PAYMENT_AUTHORIZED;
            }
            try {
                response =
                    getPaymentOperationStrategy().getOperation(abstractOrderModel.getPaymentMode(),
                                                               WirecardPaymentTransactionConstants.CAPTURE)
                                                 .doOperation(abstractOrderModel, data);
            } catch (WirecardPaymenException e) {
                LOGGER.error(e.getMessage(), e);
                return OrderStatus.PAYMENT_NOT_CAPTURED;
            }
        } else if (wirecardTransactionService.lookForAcceptedTransactions(abstractOrderModel, PaymentTransactionType.DEBIT_GET_URL)
            || wirecardTransactionService.lookForAcceptedTransactions(abstractOrderModel, PaymentTransactionType.DEBIT)) {
            return OrderStatus.PAYMENT_CAPTURED;
        }
        return wirecardPaymentService.getOrderStatus(response);
    }

    @Override
    public ReturnStatus executePaymentRefundOperation(ReturnRequestModel returnRequestModel, PaymentOperationData data) {
        Payment response = null;
        try {
            if (wirecardTransactionService.lookForAcceptedTransactions(returnRequestModel.getOrder(), PaymentTransactionType.CAPTURE)) {

                response =
                    getPaymentOperationStrategy().getOperation(returnRequestModel.getOrder().getPaymentMode(),
                                                               WirecardPaymentTransactionConstants.REFUND_FOLLOW_ON)
                                                 .doOperation(returnRequestModel, data);

            } else if (wirecardTransactionService.lookForAcceptedTransactions(returnRequestModel.getOrder(),
                                                                              PaymentTransactionType.DEBIT)) {

                WirecardPaymentConfigurationModel paymentConfigurationModel =
                    wirecardPaymentConfigurationService.getConfiguration(returnRequestModel.getOrder().getStore(),
                                                                         returnRequestModel.getOrder().getPaymentMode());
                data = new PaymentOperationData();
                data.setWirecardFallbackAuthenticationModel(paymentConfigurationModel.getFallbackAuthentication());
                response =
                    getPaymentOperationStrategy().getOperation(returnRequestModel.getOrder().getPaymentMode(),
                                                               WirecardPaymentTransactionConstants.REFUND_FOLLOW_ON)
                                                 .doOperation(returnRequestModel, data);

            }
        } catch (WirecardPaymenException e) {
            LOGGER.error(e.getMessage(), e);
        }

        ReturnStatus returnStatus = wirecardPaymentService.getReturnStatus(response);
        wirecardPaymentService.setReturnRequestStatus(returnRequestModel, returnStatus);
        return returnStatus;
    }

    @Override
    public OrderStatus executeAuthorizationCancelOperation(AbstractOrderModel abstractOrderModel) {
        Payment response = null;
        try {
            if (wirecardTransactionService.lookForAcceptedTransactions(abstractOrderModel, PaymentTransactionType.AUTHORIZATION)) {

                if (!PaymentMethodName.SEPADIRECTDEBIT.value().equals(abstractOrderModel.getPaymentMode().getPaymentAlias())) {
                    response =
                        getPaymentOperationStrategy().getOperation(abstractOrderModel.getPaymentMode(),
                                                                   WirecardPaymentTransactionConstants.CANCEL)
                                                     .doOperation(abstractOrderModel, null);
                } else {
                    return OrderStatus.CANCELLED;
                }


            } else if (wirecardTransactionService.lookForAcceptedTransactions(abstractOrderModel, PaymentTransactionType.DEBIT)) {

                WirecardPaymentConfigurationModel paymentConfigurationModel =
                    wirecardPaymentConfigurationService.getConfiguration(abstractOrderModel.getStore(),
                                                                         abstractOrderModel.getPaymentMode());
                PaymentOperationData data = new PaymentOperationData();
                data.setWirecardFallbackAuthenticationModel(paymentConfigurationModel.getFallbackAuthentication());
                response =
                    getPaymentOperationStrategy().getOperation(abstractOrderModel.getPaymentMode(),
                                                               WirecardPaymentTransactionConstants.CANCEL)
                                                 .doOperation(abstractOrderModel, data);

            }
        } catch (WirecardPaymenException e) {
            LOGGER.error(e.getMessage(), e);
            return OrderStatus.CANCELLING;
        }
        return wirecardPaymentService.getOrderStatus(response);
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }

    protected WirecardPaymentService getWirecardPaymentService() {
        return wirecardPaymentService;
    }

    @Required
    public void setWirecardPaymentModeService(WirecardPaymentModeService wirecardPaymentModeService) {
        this.wirecardPaymentModeService = wirecardPaymentModeService;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentService(WirecardPaymentService wirecardPaymentService) {
        this.wirecardPaymentService = wirecardPaymentService;
    }

    protected WirecardPaymentModeService getWirecardPaymentModeService() {
        return wirecardPaymentModeService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }

    protected PaymentOperationStrategy getPaymentOperationStrategy() {
        return paymentOperationStrategy;
    }

    @Required
    public void setPaymentOperationStrategy(PaymentOperationStrategy paymentOperationStrategy) {
        this.paymentOperationStrategy = paymentOperationStrategy;
    }
}
