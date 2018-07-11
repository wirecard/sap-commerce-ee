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

import com.wirecard.hybris.core.data.types.OrderItem;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.strategy.WirecardTaxCalculatorStrategy;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.util.TaxValue;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderItemsPopulatorTest {

    @InjectMocks
    private OrderItemsPopulator orderItemsPopulator;

    @Mock
    private AbstractOrderModel source;
    @Mock
    private CurrencyModel currency;
    @Mock
    private AbstractOrderEntryModel entry1;
    @Mock
    private ProductModel product1;
    @Mock
    private AbstractOrderEntryModel entry2;
    @Mock
    private ProductModel product2;
    @Mock
    private DeliveryModeModel deliveryMode;
    @Mock
    private WirecardTaxCalculatorStrategy wirecardTaxCalculatorStrategy;
    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    private TaxValue taxValue1;
    private TaxValue taxValue2;
    private List<AbstractOrderEntryModel> entries;
    private String currencyIsocode;
    private String deliveryModeCode;
    private String deliveryModeName;
    private String productCode1;
    private String productName1;
    private String productCode2;
    private String productName2;
    private Long productQuantity1;
    private Long productQuantity2;
    private Double totalPrice1;
    private Double totalPrice2;
    private Double deliveryCost;
    private Double totalTax;

    @Before
    public void setup() {

        currencyIsocode = "EUR";
        when(source.getCurrency()).thenReturn(currency);
        when(currency.getIsocode()).thenReturn(currencyIsocode);

        deliveryModeCode = "DM01";
        deliveryModeName = "DeliveryMode01";
        when(source.getDeliveryMode()).thenReturn(deliveryMode);
        when(deliveryMode.getCode()).thenReturn(deliveryModeCode);
        when(deliveryMode.getName()).thenReturn(deliveryModeName);
        when(wirecardPaymentConfigurationService.hasNoDiscounts(source)).thenReturn(true);

        deliveryCost = 10D;
        when(source.getDeliveryCost()).thenReturn(deliveryCost);

        totalTax = 21.42D + 6.5D;
        when(source.getTotalTax()).thenReturn(totalTax);

        taxValue1 = new TaxValue("TAX1", 21, false, 21, currencyIsocode);
        productCode1 = "P001";
        productName1 = "Product1";
        productQuantity1 = 1L;
        totalPrice1 = 121D;
        when(entry1.getQuantity()).thenReturn(productQuantity1);
        when(entry1.getTotalPrice()).thenReturn(totalPrice1);
        when(entry1.getTaxValuesInternal()).thenReturn(taxValue1.toString());
        when(entry1.getProduct()).thenReturn(product1);
        when(product1.getCode()).thenReturn(productCode1);
        when(product1.getName()).thenReturn(productName1);

        taxValue2 = new TaxValue("TAX2", 0.21, false, 0.42, currencyIsocode);
        productCode2 = "P002";
        productName2 = "Product2";
        productQuantity2 = 2L;
        totalPrice2 = 200.42D;
        when(entry2.getQuantity()).thenReturn(productQuantity2);
        when(entry2.getTotalPrice()).thenReturn(totalPrice2);
        when(entry2.getTaxValuesInternal()).thenReturn(taxValue2.toString());
        when(entry2.getProduct()).thenReturn(product2);
        when(product2.getCode()).thenReturn(productCode2);
        when(product2.getName()).thenReturn(productName2);
    }

    @Test
    public void populateTest() {
        Payment target = new Payment();

        entries = new ArrayList<>();
        entries.add(entry1);
        entries.add(entry2);
        when(source.getEntries()).thenReturn(entries);

        orderItemsPopulator.populate(source, target);

        List<OrderItem> orderItems = target.getOrderItems().getOrderItem();
        assertNotNull("Order item list should not be null", orderItems);
        assertEquals("Order item list size does not match", (entries.size() + 1), orderItems.size());

        OrderItem orderItem1 = orderItems.get(0);
        assertEquals("Entry name does not match", productName1, orderItem1.getName());
        assertEquals("Entry description does not match", StringUtils.EMPTY, orderItem1.getDescription());
        assertEquals("Product code does not match", productCode1, orderItem1.getArticleNumber());
        assertEquals("Product quantity does not match", productQuantity1.shortValue(), orderItem1.getQuantity());
        assertNotNull("Product amount should not be null", orderItem1.getAmount());
        assertEquals("Currency does not match", currencyIsocode, orderItem1.getAmount().getCurrency());
        assertEquals("Emtry amount does not match", "121.00", orderItem1.getAmount().getValue().toString());

        OrderItem orderItem2 = orderItems.get(1);
        assertEquals("Entry name does not match", productName2, orderItem2.getName());
        assertEquals("Entry description does not match", StringUtils.EMPTY, orderItem2.getDescription());
        assertEquals("Product code does not match", productCode2, orderItem2.getArticleNumber());
        assertEquals("Product quantity does not match", productQuantity2.shortValue(), orderItem2.getQuantity());
        assertNotNull("Product amount should not be null", orderItem2.getAmount());
        assertEquals("Currency does not match", currencyIsocode, orderItem2.getAmount().getCurrency());
        assertEquals("Emtry amount does not match", "100.21", orderItem2.getAmount().getValue().toString());

        OrderItem deliveryCost = orderItems.get(2);
        assertEquals("Entry name does not match", deliveryModeName, deliveryCost.getName());
        assertEquals("Entry description does not match", StringUtils.EMPTY, deliveryCost.getDescription());
        assertEquals("Product code does not match", deliveryModeCode, deliveryCost.getArticleNumber());
        assertEquals("Product quantity does not match", (short) 1, deliveryCost.getQuantity());
        assertNotNull("Product amount should not be null", deliveryCost.getAmount());
        assertEquals("Currency does not match", currencyIsocode, deliveryCost.getAmount().getCurrency());
        assertEquals("Emtry amount does not match", "10.00", deliveryCost.getAmount().getValue().toString());
    }


}
