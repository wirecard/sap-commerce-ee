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

import com.wirecard.hybris.core.data.types.AccountHolder;
import com.wirecard.hybris.core.data.types.Consumer;
import com.wirecard.hybris.core.data.types.EntryMode;
import com.wirecard.hybris.core.data.types.Money;
import com.wirecard.hybris.core.data.types.OrderItem;
import com.wirecard.hybris.core.data.types.OrderItems;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.Shipping;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPaymentAuthorizationPopulatorTest extends OmniPaymentPopulatorTest {

    @InjectMocks
    private PersonalDataPaymentPopulator personalDataPaymentPopulator;

    @Mock
    private AddressModel addressModel;
    @Mock
    private Consumer consumer;
    @Mock
    private Shipping shipping;
    @Mock
    private AccountHolder accountHolder;
    @Mock
    private CurrencyModel currency;
    @Mock
    private OrderItems orderItems;
    @Mock
    private OrderItem orderItem;
    @Mock
    private Money money;

    private BigDecimal orderItemValue;
    private Short itemQuantity;

    @Override
    @Before
    public void setup() {
        super.setup();
        when(source.getCode())
            .thenReturn("001");

        when(source.getGuid())
            .thenReturn("001");

        when(source.getTotalPrice())
            .thenReturn(40D);

        when(currency.getIsocode())
            .thenReturn("EUR");

        when(source.getCurrency())
            .thenReturn(currency);

        when(wirecardPaymentConfigurationService.getWebShop())
            .thenReturn("web");

        when(source.getDeliveryAddress())
            .thenReturn(addressModel);

        when(source.getPaymentAddress())
            .thenReturn(addressModel);

        when(consumerConverter.convert(addressModel))
            .thenReturn(consumer);

        when(shippingConverter.convert(addressModel))
            .thenReturn(shipping);

        when(accountHolderConverter.convert(addressModel))
            .thenReturn(accountHolder);

        when(orderItemsConverter.convert(source))
            .thenReturn(orderItems);

        when(orderItems.getOrderItem())
            .thenReturn(Collections.singletonList(orderItem));

        itemQuantity = 2;
        when(orderItem.getQuantity())
            .thenReturn(itemQuantity);

        when(orderItem.getAmount())
            .thenReturn(money);

        orderItemValue = new BigDecimal("20.0");
        when(money.getValue())
            .thenReturn(orderItemValue);

        when(descriptorGenerateStrategy.getDescriptor(source)).thenReturn("web 001");
    }


    @Override
    @Test
    public void populateTest() {

        Payment target = new Payment();

        paymentPopulator.populate(source, target);
        personalDataPaymentPopulator.populate(source, target);
        assertEquals("Order number does not match", "001", target.getOrderNumber());
        assertEquals("Consumer does not match", consumer, target.getConsumer());
        assertEquals("Descriptor does not match", "web 001", target.getDescriptor());
        assertEquals("Shipping does not match", shipping, target.getShipping());
        assertEquals("Account Holder does not match", accountHolder, target.getAccountHolder());
        assertEquals("Entry Mode does not match", EntryMode.ECOMMERCE, target.getEntryMode());
        assertNotNull("RequestedAmount is null", target.getRequestedAmount());
        assertEquals("RequestedAmount Currency does not match", "EUR", target.getRequestedAmount().getCurrency());
        assertEquals("RequestedAmount Value does not match", "40.0", target.getRequestedAmount().getValue().toString());
    }

}
