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

package com.wirecard.hybris.core.dao;

import com.wirecard.hybris.core.dao.impl.DefaultWirecardPaymentConfigurationDao;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.search.impl.SearchResultImpl;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardPaymentConfigurationDaoTest {

    @InjectMocks
    private DefaultWirecardPaymentConfigurationDao wirecardPaymentConfigurationDao;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private BaseStoreModel store;

    @Mock
    private PaymentModeModel paymentMode;

    private SearchResult<Object> emptyResult;
    private SearchResult<Object> validResult;
    private WirecardPaymentConfigurationModel validConfig;

    @Before
    public void setup() throws WirecardPaymenException {
        int requestedCount = 1;
        int requestedStart = 0;

        emptyResult = new SearchResultImpl<>(null, 0, requestedCount, requestedStart);

        validConfig = new WirecardPaymentConfigurationModel();
        List<Object> resultList = Collections.singletonList(validConfig);
        validResult = new SearchResultImpl<>(resultList, 1, requestedCount, requestedStart);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaymentConfigsForNullBaseStore() {
        wirecardPaymentConfigurationDao.getWirecardPaymentConfigurations(null);
    }

    @Test
    public void testNoPaymentConfigForBaseStore() {
        when(store.getPaymentConfigurations()).thenReturn(Collections.emptyList());

        List<WirecardPaymentConfigurationModel> configurations = wirecardPaymentConfigurationDao.getWirecardPaymentConfigurations(store);

        assertNotNull("Configuration list is null", configurations);
        assertTrue("Configuration list is not empty", configurations.isEmpty());
    }

    @Test
    public void testPaymentConfigsForBaseStore() {
        when(store.getPaymentConfigurations()).thenReturn(Collections.singletonList(validConfig));

        List<WirecardPaymentConfigurationModel> configurations = wirecardPaymentConfigurationDao.getWirecardPaymentConfigurations(store);

        assertNotNull("Configuration list is null", configurations);
        assertTrue("Configuration list does not contain expected configuration", configurations.contains(validConfig));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaymentConfigForNullBaseStoreAndPaymentMode() {
        wirecardPaymentConfigurationDao.getWirecardPaymentConfiguration(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaymentConfigForNullPaymentMode() {
        wirecardPaymentConfigurationDao.getWirecardPaymentConfiguration(store, null);
    }

    @Test
    public void testNoPaymentConfigForBaseStoreAndPaymentMode() {
        when(flexibleSearchService.search(Mockito.anyObject(), Mockito.anyMapOf(String.class, ItemModel.class))).thenReturn(emptyResult);

        WirecardPaymentConfigurationModel configuration =
            wirecardPaymentConfigurationDao.getWirecardPaymentConfiguration(store, paymentMode);

        assertNull("Configuration is not null", configuration);
    }

    @Test
    public void testPaymentConfigsForBaseStoreAndPaymentMode() {
        when(flexibleSearchService.search(Mockito.anyObject(), Mockito.anyMapOf(String.class, ItemModel.class))).thenReturn(validResult);

        WirecardPaymentConfigurationModel configuration =
            wirecardPaymentConfigurationDao.getWirecardPaymentConfiguration(store, paymentMode);

        assertNotNull("Configuration is null", configuration);
        assertEquals("Configuration does not match", validConfig, configuration);
    }
}
