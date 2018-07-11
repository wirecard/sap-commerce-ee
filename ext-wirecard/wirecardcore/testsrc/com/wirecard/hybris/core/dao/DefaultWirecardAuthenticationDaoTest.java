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

import com.wirecard.hybris.core.dao.impl.DefaultWirecardAuthenticationDao;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardAuthenticationDaoTest {

    @InjectMocks
    private DefaultWirecardAuthenticationDao wirecardAuthenticationDao;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    @Mock
    private BaseStoreModel store;

    @Mock
    private PaymentModeModel paymentMode;

    private String baseURL;

    @Mock
    private SearchResult<Object> searchResult;

    @Mock
    private List authenticationModelList;

    @Mock
    private WirecardAuthenticationModel authenticationModel;

    @Before
    public void setup() throws WirecardPaymenException {
        when(flexibleSearchService.search(Mockito.anyString(), Mockito.anyMapOf(String.class, ItemModel.class))).thenReturn(searchResult);
        when(searchResult.getResult()).thenReturn(authenticationModelList);
        when(authenticationModelList.get(0)).thenReturn(authenticationModel);
        when(authenticationModel.getBaseUrl()).thenReturn(baseURL);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaymentConfigForNullBaseStoreAndPaymentMode() {
        wirecardAuthenticationDao.getWirecardPaymentBaseURL(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaymentConfigForNullPaymentMode() {
        wirecardAuthenticationDao.getWirecardPaymentBaseURL(store, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaymentConfigsForNullBaseStore() {
        wirecardAuthenticationDao.getWirecardPaymentBaseURL(null, paymentMode);
    }

    @Test
    public void testGetWirecardPaymentBaseURL() {

        String baseURL =
            wirecardAuthenticationDao.getWirecardPaymentBaseURL(store, paymentMode);

        assertEquals("Response is not equals",this.baseURL, baseURL);
    }

}
