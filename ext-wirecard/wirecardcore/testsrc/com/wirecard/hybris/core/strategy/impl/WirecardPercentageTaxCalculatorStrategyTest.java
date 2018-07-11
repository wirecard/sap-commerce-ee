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

import com.wirecard.hybris.core.data.types.Money;
import com.wirecard.hybris.core.data.types.OrderItem;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.util.TaxValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WirecardPercentageTaxCalculatorStrategyTest {

    private static final String CURRENCY_EUR = "EUR";

    @InjectMocks
    private WirecardPercentageTaxCalculatorStrategy wirecardPercentageTaxCalculatorStrategy;

    @Test
    public void calculateItemTaxes() throws Exception {
        BigDecimal startingItemTaxes = BigDecimal.TEN;
        Double entryTotalPrice = 240D;
        int entryTaxPercentage = 20;
        int entryTaxTotalAmount = 40;
        long entryQuantity = 5L;
        String orderItemTaxRate = "20.0000";
        String finalItemTaxes = "50.0000";

        TaxValue taxValue = new TaxValue("full-taxes", entryTaxPercentage, false, entryTaxTotalAmount, CURRENCY_EUR);
        AbstractOrderEntryModel entry = new AbstractOrderEntryModel();
        entry.setTaxValues(Collections.singletonList(taxValue));
        entry.setTaxValuesInternal(taxValue.toString());
        entry.setQuantity(entryQuantity);
        entry.setTotalPrice(entryTotalPrice);

        OrderItem orderItem = new OrderItem();
        BigDecimal accumulatedTaxes = wirecardPercentageTaxCalculatorStrategy.calculateItemTaxes(entry, orderItem, startingItemTaxes);

        assertEquals("Order item tax rate does not match", orderItemTaxRate, orderItem.getTaxRate().toString());
        assertEquals("Total tax amount does not match", finalItemTaxes, accumulatedTaxes.toString());
    }

    @Test
    public void calculateDeliveryTaxes() throws Exception {
        Double orderTaxes = 12D;
        BigDecimal itemTaxes = BigDecimal.TEN;
        BigDecimal deliveryAmount = BigDecimal.valueOf(20);
        String deliveryTaxes = "10.00";

        AbstractOrderModel order = new OrderModel();
        order.setTotalTax(orderTaxes);

        OrderItem orderItem = new OrderItem();
        Money orderItemAmount = new Money();
        orderItemAmount.setValue(deliveryAmount);
        orderItem.setAmount(orderItemAmount);

        wirecardPercentageTaxCalculatorStrategy.calculateDeliveryTaxes(order, orderItem, itemTaxes);

        assertEquals("Delivery tax rate does not match", deliveryTaxes, orderItem.getTaxRate().toString());
    }

}
