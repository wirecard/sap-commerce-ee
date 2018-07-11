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
import com.wirecard.hybris.core.operation.PaymentOperation;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.exception.WirecardTriggerOperationFallbackException;
import de.hybris.platform.core.model.ItemModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * @author cprobst
 *
 */
public class FallbackPaymentOperation<T extends ItemModel> implements PaymentOperation<T> {

    private static final Logger LOG = Logger.getLogger(FallbackPaymentOperation.class);

    private PaymentOperation<T> decisionOperation;

    private PaymentOperation<T> successOperation;

    private PaymentOperation<T> errorOperation;

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment doOperation(T operationData, PaymentOperationData data) throws WirecardPaymenException {
        try {
            Payment payment = decisionOperation.doOperation(operationData, data);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Decision operation was succesful. Fallback will not be triggered.");
            }
            if (successOperation != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Success operation will be triggered.");
                }
                return successOperation.doOperation(operationData, data);
            }
            return payment;
        } catch (WirecardTriggerOperationFallbackException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Decision operation failed. Fallback will be triggered.", e);
            }
            if (errorOperation != null) {
                return errorOperation.doOperation(operationData, data);
            } else {
                throw new WirecardPaymenException("No fallback operation availabel", e);
            }
        }
    }

    @Required
    public void setDecisionOperation(PaymentOperation<T> decisionOperation) {
        this.decisionOperation = decisionOperation;
    }

    public void setSuccessOperation(PaymentOperation<T> successOperation) {
        this.successOperation = successOperation;
    }

    public void setErrorOperation(PaymentOperation<T> errorOperation) {
        this.errorOperation = errorOperation;
    }

}
