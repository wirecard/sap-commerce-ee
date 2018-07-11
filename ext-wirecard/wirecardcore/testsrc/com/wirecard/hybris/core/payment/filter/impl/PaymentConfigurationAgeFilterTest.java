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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author jmateos
 */

@UnitTest
@RunWith(Parameterized.class)
public class PaymentConfigurationAgeFilterTest {

    private static Collection<Object[]> data;
    private static Calendar birthOfDate;

    static {

        data = new ArrayList<>();
        birthOfDate = Calendar.getInstance();

        data.add(getTestdata(null, null, true)); // no age given filter disabled

        data.add(getTestdata(birthOfDate, null, true)); // no bound

        data.add(getTestdata(birthOfDate, (short) 0, true)); // bound 0

        data.add(getTestdata(null, (short) 18, false)); // no age given birthOfDate

        birthOfDate.set(2010, 12, 15); //8 years
        data.add(getTestdata(birthOfDate, (short) 18, false)); // not enough age
        birthOfDate.set(1988, 1, 15); //30 years
        data.add(getTestdata(birthOfDate, (short) 18, true)); // enough age

        birthOfDate.set(Calendar.getInstance().get(Calendar.YEAR) -18,
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE)); //exactly 18 years
        data.add(getTestdata(birthOfDate, (short) 18, true));// birth today

        birthOfDate.set(Calendar.getInstance().get(Calendar.YEAR) -18,
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DATE),
                        12,
                        10,
                        7); //exactly 18 years with time
        data.add(getTestdata(birthOfDate, (short) 18, true));// birth today

    }

    static Object[] getTestdata(Calendar birthOfDate, Short years, boolean expected) {
        CartModel cart = Mockito.mock(CartModel.class);
        AddressModel address = Mockito.mock(AddressModel.class);
        Mockito.when(cart.getDeliveryAddress()).thenReturn(address);
        Mockito.when(address.getDateOfBirth()).thenReturn(birthOfDate == null ? null : birthOfDate.getTime());
        WirecardPaymentConfigurationModel configuration = Mockito.mock(WirecardPaymentConfigurationModel.class);
        Mockito.when(configuration.getYears()).thenReturn(years);
        return new Object[]{cart, configuration, expected};
    }

    @Parameters
    public static Collection<Object[]> data() {
        return data;
    }

    public PaymentConfigurationAgeFilterTest(CartModel cart, WirecardPaymentConfigurationModel configuration,
                                             Boolean expected) {
        this.cart = cart;
        this.configuration = configuration;
        this.expected = expected;
    }

    @InjectMocks
    private PaymentConfigurationAgeFilter paymentConfigurationAgeFilter;

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
        assertEquals(expected, paymentConfigurationAgeFilter.isValid(configuration, cart));
    }

}
