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
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(Parameterized.class)
public class PaymentConfigurationDigitalProductFilterTest {

    private static Collection<Object[]> data;
    private static Calendar birthOfDate;

    static {

        data = new ArrayList<>();

        data.add(getTestdata(Boolean.TRUE, false, true)); // filter disabled
        data.add(getTestdata(null, true, true)); // digital field not set
        data.add(getTestdata(Boolean.FALSE, true, true)); // physical product
        data.add(getTestdata(Boolean.TRUE, true, false)); // digital product

    }

    static Object[] getTestdata(Boolean isDigital, boolean isRestrictionEnabled, boolean expected) {
        ProductModel product = Mockito.mock(ProductModel.class);
        Mockito.when(product.getDigital()).thenReturn(isDigital);

        AbstractOrderEntryModel entry = Mockito.mock(AbstractOrderEntryModel.class);
        Mockito.when(entry.getProduct()).thenReturn(product);

        List<AbstractOrderEntryModel> entries = Collections.singletonList(entry);
        CartModel cart = Mockito.mock(CartModel.class);

        Mockito.when(cart.getEntries()).thenReturn(entries);

        WirecardPaymentConfigurationModel configuration = Mockito.mock(WirecardPaymentConfigurationModel.class);
        Mockito.when(configuration.getRestrictDigitalProducts()).thenReturn(isRestrictionEnabled);

        return new Object[]{cart, configuration, expected};
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return data;
    }

    public PaymentConfigurationDigitalProductFilterTest(CartModel cart, WirecardPaymentConfigurationModel configuration,
                                             Boolean expected) {
        this.cart = cart;
        this.configuration = configuration;
        this.expected = expected;
    }

    @InjectMocks
    private PaymentConfigurationDigitalProductFilter paymentConfigurationDigitalProductFilter;

    private CartModel cart;
    private WirecardPaymentConfigurationModel configuration;
    boolean expected;

    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        assertEquals(expected, paymentConfigurationDigitalProductFilter.isValid(configuration, cart));
    }

}
