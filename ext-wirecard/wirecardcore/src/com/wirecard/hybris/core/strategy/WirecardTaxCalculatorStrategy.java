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

package com.wirecard.hybris.core.strategy;

import com.wirecard.hybris.core.data.types.OrderItem;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.math.BigDecimal;

public interface WirecardTaxCalculatorStrategy {

    /**
     * Sums order entry taxes and returns the accumulated value.
     *
     * @param entry
     *     cart or order entry for which the taxes are calculated
     * @param orderItem
     *     Payment object node to be updated with tax info
     * @param totalItemTaxes
     *     starting tax value to be summed
     * @return the sum of the starting tax value plus the given entry taxes
     */
    BigDecimal calculateItemTaxes(final AbstractOrderEntryModel entry, final OrderItem orderItem, BigDecimal totalItemTaxes);

    /**
     * Calculates the order delivery taxes for a given order and updates the related Payment object node with that information
     *
     * @param abstractOrderModel
     *     the order whose delivery taxes are being calculated
     * @param orderItem
     *     Payment object node to be updated with tax info
     * @param totalItemTaxes
     *     total tax amount for all order items
     */
    void calculateDeliveryTaxes(final AbstractOrderModel abstractOrderModel, final OrderItem orderItem, BigDecimal totalItemTaxes);

}
