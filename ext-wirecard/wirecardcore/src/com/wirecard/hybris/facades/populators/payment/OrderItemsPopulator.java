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

package com.wirecard.hybris.facades.populators.payment;

import com.wirecard.hybris.core.data.types.Money;
import com.wirecard.hybris.core.data.types.OrderItem;
import com.wirecard.hybris.core.data.types.OrderItems;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.strategy.WirecardTaxCalculatorStrategy;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsPopulator extends AbstractOrderAwarePaymentPopulator {

    private static final int DECIMAL_DIGITS_MAX = 2;
    private WirecardTaxCalculatorStrategy wirecardTaxCalculatorStrategy;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Override
    public void doPopulate(AbstractOrderModel source, Payment target) throws ConversionException {
        if (getWirecardPaymentConfigurationService().hasNoDiscounts(source)) {
            List<OrderItem> orderItemList = fillOrderItems(source);
            OrderItems orderItems = new OrderItems();
            orderItems.getOrderItem().addAll(orderItemList);
            target.setOrderItems(orderItems);
        }
    }


    private List<OrderItem> fillOrderItems(AbstractOrderModel abstractOrderModel) {

        List<OrderItem> orderItemsList = new ArrayList<>(abstractOrderModel.getEntries().size() + 1);

        BigDecimal totalItemTaxes = BigDecimal.ZERO;

        for (AbstractOrderEntryModel entry : abstractOrderModel.getEntries()) {
            final String name = entry.getProduct().getName();
            final String articleNumber = entry.getProduct().getCode();
            final short quantity = entry.getQuantity().shortValue();

            Money amount = new Money();
            amount.setValue(unitPriceOrTaxForOrderEntry(BigDecimal.valueOf(entry.getTotalPrice()), entry));
            final String currency = abstractOrderModel.getCurrency().getIsocode();
            amount.setCurrency(currency);

            OrderItem orderItem = new OrderItem();
            orderItem.setName(name);
            orderItem.setDescription(StringUtils.EMPTY);
            orderItem.setArticleNumber(articleNumber);
            orderItem.setAmount(amount);
            orderItem.setQuantity(quantity);

            totalItemTaxes = getWirecardTaxCalculatorStrategy().calculateItemTaxes(entry, orderItem, totalItemTaxes);

            orderItemsList.add(orderItem);
        }

        setDeliveryCostAsOrderItem(abstractOrderModel, orderItemsList, totalItemTaxes);

        return orderItemsList;
    }

    private void setDeliveryCostAsOrderItem(AbstractOrderModel abstractOrderModel,
                                            List<OrderItem> orderItemsList,
                                            BigDecimal totalItemTaxes) {
        Double deliveryCost = abstractOrderModel.getDeliveryCost();

        //add delivery mode as cart item
        final String deliveryName = abstractOrderModel.getDeliveryMode().getName();
        final String deliveryCode = abstractOrderModel.getDeliveryMode().getCode();

        final OrderItem orderItem = new OrderItem();

        Money amount = new Money();
        amount.setValue(BigDecimal.valueOf(deliveryCost).setScale(DECIMAL_DIGITS_MAX, RoundingMode.HALF_EVEN));
        amount.setCurrency(abstractOrderModel.getCurrency().getIsocode());

        orderItem.setName(deliveryName);
        orderItem.setDescription(StringUtils.EMPTY);
        orderItem.setArticleNumber(deliveryCode);
        orderItem.setAmount(amount);
        orderItem.setQuantity((short) 1);

        getWirecardTaxCalculatorStrategy().calculateDeliveryTaxes(abstractOrderModel, orderItem, totalItemTaxes);

        orderItemsList.add(orderItem);
    }

    private BigDecimal unitPriceOrTaxForOrderEntry(final BigDecimal totalPrice, final AbstractOrderEntryModel orderEntry) {
        final BigDecimal unidades = new BigDecimal(orderEntry.getQuantity())
            .setScale(DECIMAL_DIGITS_MAX, RoundingMode.HALF_EVEN);
        return totalPrice.setScale(DECIMAL_DIGITS_MAX, RoundingMode.HALF_EVEN)
                         .divide(unidades, RoundingMode.HALF_EVEN);

    }

    protected WirecardTaxCalculatorStrategy getWirecardTaxCalculatorStrategy() {
        return wirecardTaxCalculatorStrategy;
    }

    @Required
    public void setWirecardTaxCalculatorStrategy(WirecardTaxCalculatorStrategy wirecardTaxCalculatorStrategy) {
        this.wirecardTaxCalculatorStrategy = wirecardTaxCalculatorStrategy;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }
}
