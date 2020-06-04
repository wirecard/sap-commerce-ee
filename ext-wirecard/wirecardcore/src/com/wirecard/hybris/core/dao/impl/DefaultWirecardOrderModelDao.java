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

package com.wirecard.hybris.core.dao.impl;

import com.wirecard.hybris.core.dao.WirecardOrderModelDao;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.util.ServicesUtil;

import java.util.HashMap;
import java.util.Map;


public class DefaultWirecardOrderModelDao extends AbstractItemDao implements WirecardOrderModelDao {

    private static final String CONFIG_QUERY =
        "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {"
            + OrderModel.GUID + "} = ?guid AND {" + OrderModel.VERSIONID + "} IS NULL";

    private static final String CART_QUERY =
            "SELECT {" + CartModel.PK + "} FROM {" +
                    CartModel._TYPECODE + "} WHERE {" +
                    CartModel.GUID + "} = ?guid";

    @Override
    public OrderModel getOrderModelByGuid(String guid) {
        ServicesUtil.validateParameterNotNull(guid, "Guid must not be null");

        final Map<String, Object> params = new HashMap<>();
        params.put("guid", guid);
        FlexibleSearchQuery flexibleSearchQuery =
            new FlexibleSearchQuery(CONFIG_QUERY, params);
        return getFlexibleSearchService().<OrderModel>searchUnique(flexibleSearchQuery);
    }

    @Override
    public CartModel getCartModelByGuid(String guid) {
        ServicesUtil.validateParameterNotNull(guid, "Guid must not be null");

        final Map<String, Object> params = new HashMap<>();
        params.put("guid", guid);
        FlexibleSearchQuery query = new FlexibleSearchQuery(CART_QUERY, params);
        return getFlexibleSearchService().<CartModel>searchUnique(query);
    }
}
