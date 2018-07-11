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

import com.wirecard.hybris.core.data.types.OrderItem;
import com.wirecard.hybris.core.strategy.WirecardTaxCalculatorStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.util.TaxValue;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WirecardPercentageTaxCalculatorStrategy implements WirecardTaxCalculatorStrategy {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100.0000);
    private static final int DECIMAL_DIGITS_INTERMEDIATE = 4;
    private static final int DECIMAL_DIGITS_MAX = 2;

    @Override
    public BigDecimal calculateItemTaxes(final AbstractOrderEntryModel entry, final OrderItem orderItem, BigDecimal totalItemTaxes) {

        TaxValue tax = TaxValue.parseTaxValue(entry.getTaxValuesInternal());

        BigDecimal taxRate = BigDecimal.valueOf(tax.getValue()).setScale(DECIMAL_DIGITS_INTERMEDIATE, RoundingMode.HALF_EVEN);
        orderItem.setTaxRate(taxRate);

        totalItemTaxes = totalItemTaxes.add(BigDecimal.valueOf(entry.getTotalPrice())
                                   .setScale(DECIMAL_DIGITS_INTERMEDIATE, RoundingMode.HALF_EVEN)
                                   .multiply(taxRate)
                                   .divide(HUNDRED.add(taxRate),
                                           DECIMAL_DIGITS_INTERMEDIATE,
                                           RoundingMode.HALF_EVEN));

        return totalItemTaxes;
    }

    @Override
    public void calculateDeliveryTaxes(final AbstractOrderModel abstractOrderModel,
                                       final OrderItem orderItem,
                                       BigDecimal totalItemTaxes) {

        BigDecimal taxAmount = BigDecimal.valueOf(abstractOrderModel.getTotalTax())
                                         .setScale(DECIMAL_DIGITS_INTERMEDIATE, RoundingMode.HALF_EVEN)
                                         .subtract(totalItemTaxes)
                                         .setScale(DECIMAL_DIGITS_INTERMEDIATE, RoundingMode.HALF_EVEN);

        BigDecimal deliveryTaxes =
            (HUNDRED.multiply(taxAmount)).divide(orderItem.getAmount().getValue(),
                                                 DECIMAL_DIGITS_INTERMEDIATE,
                                                 RoundingMode.HALF_EVEN);

        orderItem.setTaxRate(deliveryTaxes.setScale(DECIMAL_DIGITS_MAX, RoundingMode.HALF_EVEN));
    }
}
