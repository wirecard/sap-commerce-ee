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
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author ccano
 */

@UnitTest
@RunWith(Parameterized.class)
public class PaymentConfigurationShippingFilterTest {

    private static Collection<Object[]> data;

    static {
        CountryModel validCountry = Mockito.mock(CountryModel.class);
        CountryModel invalidCountry = Mockito.mock(CountryModel.class);
        Set<CountryModel> allowedCountries = Collections.singleton(validCountry);

        data = new ArrayList<>();
        data.add(getTestdata(null, null, true)); // no country given filter disabled
        data.add(getTestdata(validCountry, null, true)); // no allowed countries filter disabled
        data.add(getTestdata(validCountry, Collections.emptySet(), true)); // empty allowed countries filter disabled
        data.add(getTestdata(validCountry, allowedCountries, true)); // country is allowed
        data.add(getTestdata(invalidCountry, allowedCountries, false)); // country is not allowed
    }

    private static Object[] getTestdata(CountryModel country, Set<CountryModel> allowedCountries, boolean expected) {
        CartModel cart = Mockito.mock(CartModel.class);
        AddressModel deliveryAddress = Mockito.mock(AddressModel.class);
        Mockito.when(cart.getDeliveryAddress()).thenReturn(deliveryAddress);
        Mockito.when(deliveryAddress.getCountry()).thenReturn(country);
        WirecardPaymentConfigurationModel configuration = Mockito.mock(WirecardPaymentConfigurationModel.class);
        Mockito.when(configuration.getShippingCountries()).thenReturn(allowedCountries);
        return new Object[]{cart, configuration, expected};
    }

    @Parameters
    public static Collection<Object[]> data() {
        return data;
    }

    public PaymentConfigurationShippingFilterTest(CartModel cart, WirecardPaymentConfigurationModel configuration,
                                                  Boolean expected) {
        this.cart = cart;
        this.configuration = configuration;
        this.expected = expected;
    }

    @InjectMocks
    private PaymentConfigurationShippingFilter paymentConfigurationShippingFilter;

    private CartModel cart;
    private WirecardPaymentConfigurationModel configuration;
    private boolean expected;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
        assertEquals(expected, paymentConfigurationShippingFilter.isValid(configuration, cart));
    }

}
