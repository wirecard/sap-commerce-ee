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

package com.wirecard.hybris.core.payment.filter.impl;

import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.core.payment.filter.PaymentConfigurationFilter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class PaymentConfigurationTotalAmountFilter implements PaymentConfigurationFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentConfigurationTotalAmountFilter.class);

    private CommonI18NService commonI18NService;

    /**
     * Check for the totalAmount configuration in the WirecardPaymentConfigurationModel. If a currency is given the
     * currency conversion will be executed before applying the following rules. A null value will be treated as if no
     * boundary is given. The minimum amount is inclusive and the maximum amount is exclusive (min <= totalAmount < max)
     *
     * @return true if the configuration is visible to due the amount configuration else false
     */
    @Override
    public boolean isValid(WirecardPaymentConfigurationModel configuration, AbstractOrderModel order) {

        // convert the order total to the currency of amount configuration to only have one conversion
        double orderTotal = convertCurrency(order.getCurrency(), configuration.getTotalAmountCurrency(), order.getTotalPrice());

        // there was no total amount of order (deactivate filtering)
        if (Double.isNaN(orderTotal)) {
            return true;
        }

        Double min = configuration.getTotalAmountMin();
        if (min == null) {
            min = Double.MIN_VALUE;
        }
        Double max = configuration.getTotalAmountMax();
        if (max == null) {
            max = Double.MAX_VALUE;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Processing total amount threshold with (min <= orderTotal < max): {} <= {} < {}", min, orderTotal, max);
        }

        return min <= orderTotal && orderTotal < max;

    }

    protected double convertCurrency(CurrencyModel sourceCurrency, CurrencyModel targetCurrency, Double amount) {
        if (amount == null) {
            return Double.NaN;
        }

        if (sourceCurrency == null || targetCurrency == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("No conversion will be done because source or target currency where null for amount {}", amount);
            }
            return amount;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Amount to be converted {} {} to {}", amount, sourceCurrency.getIsocode(), targetCurrency.getIsocode());
        }

        final int digits = targetCurrency.getDigits();
        return getCommonI18NService().convertAndRoundCurrency(sourceCurrency.getConversion(),
                                                              targetCurrency.getConversion(), digits, amount);
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
