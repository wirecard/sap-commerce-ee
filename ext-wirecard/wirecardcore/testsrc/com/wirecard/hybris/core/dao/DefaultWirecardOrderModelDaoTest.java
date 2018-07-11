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

import com.wirecard.hybris.core.dao.impl.DefaultWirecardOrderModelDao;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultWirecardOrderModelDaoTest {

    @InjectMocks
    private DefaultWirecardOrderModelDao wirecardOrderModelDao;

    @Mock
    private FlexibleSearchService flexibleSearchService;

    private String guid;

    @Mock
    private OrderModel orderModel;

    @Before
    public void setup() throws WirecardPaymenException {
        when(flexibleSearchService.<OrderModel>searchUnique(Mockito.anyObject())).thenReturn(orderModel);
        this.guid = "testGuid";
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPaymentConfigForNullGuid() {
        wirecardOrderModelDao.getOrderModelByGuid(null);
    }

    @Test
    public void testGetOrderModelByGuid() {

        OrderModel orderModel =
            wirecardOrderModelDao.getOrderModelByGuid(guid);

        assertEquals("Response is not equals", this.orderModel, orderModel);
    }

}
