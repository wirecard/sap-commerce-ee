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

import com.wirecard.hybris.core.converter.data.WirecardPaymentInfoData;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.facades.populators.WirecardCreditCardPaymentInfoPopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PaymentConfigurationPromotionFilterTest {

    @InjectMocks
    private PaymentConfigurationPromotionFilter paymentConfigurationPromotionFilter;

    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Mock
    private WirecardPaymentConfigurationModel configuration;

    @Mock
    private AbstractOrderModel order;

    @Test
    public void testIsValid1() {

        when(configuration.getRestrictPromotions()).thenReturn(false);
        when(wirecardPaymentConfigurationService.hasNoDiscounts(order)).thenReturn(false);

        boolean result = paymentConfigurationPromotionFilter.isValid(configuration,order);

        assertEquals(""
                         + "Is Valid son't work well 1", true, result);
    }

    @Test
    public void testIsValid2() {

        when(configuration.getRestrictPromotions()).thenReturn(true);
        when(wirecardPaymentConfigurationService.hasNoDiscounts(order)).thenReturn(true);

        boolean result = paymentConfigurationPromotionFilter.isValid(configuration,order);

        assertEquals(""
                         + "Is Valid son't work well 2", true, result);
    }
}
