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

import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.store.BaseStoreModel;
import org.apache.commons.lang.LocaleUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardDescriptorGenerateStrategyTest {

    @InjectMocks
    private DefaultWirecardDescriptorGenerateStrategy defaultWirecardDescriptorGenerateStrategy;

    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Mock
    private AbstractOrderModel abstractOrderModel;

    @Mock
    private BaseStoreModel store;

    @Mock
    LanguageModel languageModel;

    private String webShop;
    private String shopName;
    private String orderNumber;
    private Locale defaultLocale;

    @Before
    public void setup() throws IOException {

        webShop = "WebShop";
        shopName = "electronics";
        orderNumber = "0001";
        defaultLocale = LocaleUtils.toLocale("en");

        when(wirecardPaymentConfigurationService.getWebShop()).thenReturn(webShop);
        when(abstractOrderModel.getStore()).thenReturn(store);
        when(store.getName()).thenReturn(shopName);
        when(abstractOrderModel.getGuid()).thenReturn(orderNumber);
        when(store.getDefaultLanguage()).thenReturn(languageModel);
        when(languageModel.getIsocode()).thenReturn("en");
        when(store.getName(defaultLocale)).thenReturn(shopName);
    }


    @Test
    public void testGetDescriptorShopName() {

        assertNotNull("A Descriptor should be returned", defaultWirecardDescriptorGenerateStrategy.getDescriptor(abstractOrderModel));
        assertEquals("Descriptor does not match",
                     "electroni 0001", defaultWirecardDescriptorGenerateStrategy.getDescriptor(abstractOrderModel));

    }

    @Test
    public void testGetDescriptorWebShop() {

        when(store.getName()).thenReturn(null);

        assertNotNull("A Descriptor should be returned", defaultWirecardDescriptorGenerateStrategy.getDescriptor(abstractOrderModel));
        assertEquals("Descriptor does not match",
                     "WebShop 0001", defaultWirecardDescriptorGenerateStrategy.getDescriptor(abstractOrderModel));

    }

}
