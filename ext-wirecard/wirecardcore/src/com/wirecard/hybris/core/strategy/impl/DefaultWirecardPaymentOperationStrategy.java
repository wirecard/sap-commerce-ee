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

import com.wirecard.hybris.core.constants.WirecardPaymentTransactionConstants;
import com.wirecard.hybris.core.operation.PaymentOperation;
import com.wirecard.hybris.core.strategy.PaymentOperationStrategy;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.exception.constants.WirecardPaymentExceptionConstants;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DefaultWirecardPaymentOperationStrategy implements PaymentOperationStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWirecardPaymentOperationStrategy.class);

    private static final String GET_OPERATION_LOG_MSG = "geting operation for {} {}";

    private Map<String, Map<String, PaymentOperation>> converterMap;

    @Override
    public PaymentOperation getOperation(PaymentModeModel paymentModeModel) throws WirecardPaymenException {
        return getOperation(paymentModeModel, WirecardPaymentTransactionConstants.AUTHORIZATION);
    }

    @Override
    public PaymentOperation getOperation(PaymentModeModel paymentModeModel, String paymentTransaction) throws WirecardPaymenException {
        LOG.info(GET_OPERATION_LOG_MSG, paymentModeModel.getCode(), paymentTransaction);
        String provider = paymentModeModel.getCode();
        Map<String, PaymentOperation> operationMap = converterMap.get(provider);

        PaymentOperation paymentOperation = operationMap.get(paymentTransaction);

        if (paymentOperation == null) {
            throw new WirecardPaymenException(WirecardPaymentExceptionConstants.NULL_OPERATION_ERROR);
        } else {
            return paymentOperation;
        }
    }

    public Map<String, Map<String, PaymentOperation>> getConverterMap() {
        return converterMap;
    }

    public void setConverterMap(Map<String, Map<String, PaymentOperation>> converterMap) {
        this.converterMap = converterMap;
    }
}
