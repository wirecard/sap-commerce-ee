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
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.i18n.impl.DefaultCommonI18NService;
import de.hybris.platform.servicelayer.i18n.impl.DefaultConversionStrategy;
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
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author cprobst
 *
 */

@UnitTest
@RunWith(Parameterized.class)
public class PaymentConfigurationTotalAmountFilterTest {

    private static Collection<Object[]> data;

    public static CurrencyModel euroCurrency;

    public static CurrencyModel usdCurrency;

    static {
        euroCurrency = Mockito.mock(CurrencyModel.class);
        usdCurrency = Mockito.mock(CurrencyModel.class);
        Mockito.when(usdCurrency.getDigits()).thenReturn(2);
        Mockito.when(euroCurrency.getDigits()).thenReturn(2);
        Mockito.when(euroCurrency.getConversion()).thenReturn(1d);
        Mockito.when(usdCurrency.getConversion()).thenReturn(10d);

        data = new ArrayList<>();

        data.add(getTestdata(null, euroCurrency, 10.0d, 100.1d, usdCurrency, true)); // no amount given filter disabled

        // with currency conversion 1 euro == 10 dollar
        data.add(getTestdata(10.0d, euroCurrency, 10.0d, 100.1d, usdCurrency, true)); // no bound hit
        data.add(getTestdata(10.0d, euroCurrency, 10.0d, 100.0d, usdCurrency, false)); // upper bound hit
        data.add(getTestdata(100.0d, euroCurrency, null, null, usdCurrency, true)); // no bounds
        data.add(getTestdata(100.0d, euroCurrency, null, 1000.0d, usdCurrency, false)); // upper bound hit
        data.add(getTestdata(100.0d, euroCurrency, null, 1001.0d, usdCurrency, true)); // upper bound not hit
        data.add(getTestdata(100.0d, euroCurrency, 1000.0d, null, usdCurrency, true)); // lower bound not hit
        data.add(getTestdata(99.99d, euroCurrency, 1000.0d, null, usdCurrency, false)); // lower bound hit

        // no currency conversion
        data.add(getTestdata(100.0d, euroCurrency, null, null, euroCurrency, true)); // no bounds
        data.add(getTestdata(100.0d, euroCurrency, null, 100.0d, euroCurrency, false)); // hit upper bound
        data.add(getTestdata(100.0d, euroCurrency, null, 101.0d, euroCurrency, true)); // no bound hit
        data.add(getTestdata(100.0d, euroCurrency, 100.0d, null, euroCurrency, true)); // lower bound not hit no upper
        data.add(getTestdata(99.0d, euroCurrency, 100.0d, null, euroCurrency, false)); // lower bound hit no upper bound
        data.add(getTestdata(100.0d, euroCurrency, 50d, 100.00d, euroCurrency, false)); // upper bound hit
        data.add(getTestdata(100.0d, euroCurrency, 100d, 1000.00d, euroCurrency, true)); // no bound hit
        data.add(getTestdata(99.99d, euroCurrency, 100d, 1000.00d, euroCurrency, false)); // lower bound hit
        data.add(getTestdata(100.0d, euroCurrency, 50d, 100.01d, euroCurrency, true)); // no bound hit
        data.add(getTestdata(100.0d, euroCurrency, 100.0d, 100.0d, euroCurrency, false)); // hit upper bound
        data.add(getTestdata(100.0d, euroCurrency, 10.0d, 99.99d, euroCurrency, false)); // hit upper bound


    }

    static Object[] getTestdata(Double totalAmount, CurrencyModel cartCurrency, Double minAmount, Double maxAmount,
            CurrencyModel configCurrency, boolean expected) {
        CartModel cart = Mockito.mock(CartModel.class);
        Mockito.when(cart.getTotalPrice()).thenReturn(totalAmount);
        Mockito.when(cart.getCurrency()).thenReturn(cartCurrency);
        WirecardPaymentConfigurationModel configuration = Mockito.mock(WirecardPaymentConfigurationModel.class);
        Mockito.when(configuration.getTotalAmountMin()).thenReturn(minAmount);
        Mockito.when(configuration.getTotalAmountMax()).thenReturn(maxAmount);
        Mockito.when(configuration.getTotalAmountCurrency()).thenReturn(configCurrency);
        return new Object[] { cart, configuration, expected };
    }

    @Parameters
    public static Collection<Object[]> data() {
        return data;
    }

    public PaymentConfigurationTotalAmountFilterTest(CartModel cart, WirecardPaymentConfigurationModel configuration,
                                                         Boolean expected) {
        this.cart = cart;
        this.configuration = configuration;
        this.expected = expected;
    }

    @InjectMocks
    private PaymentConfigurationTotalAmountFilter paymentConfigurationTotalAmountFilter;

    private DefaultCommonI18NService commonI18NService;

    private CartModel cart;
    private WirecardPaymentConfigurationModel configuration;
    boolean expected;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        commonI18NService = new DefaultCommonI18NService();
        commonI18NService.setConversionStrategy(new DefaultConversionStrategy());
        paymentConfigurationTotalAmountFilter.setCommonI18NService(commonI18NService);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        assertEquals(expected, paymentConfigurationTotalAmountFilter.isValid(configuration, cart));
    }

}
