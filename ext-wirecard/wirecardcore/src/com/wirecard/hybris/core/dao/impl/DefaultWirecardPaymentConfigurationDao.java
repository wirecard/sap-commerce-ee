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

import com.wirecard.hybris.core.dao.WirecardPaymentConfigurationDao;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.BaseStoreModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultWirecardPaymentConfigurationDao extends AbstractItemDao implements WirecardPaymentConfigurationDao {

    private static final String CONFIG_LIST_QUERY =
        "SELECT {" + WirecardPaymentConfigurationModel.PK + "} FROM {" + WirecardPaymentConfigurationModel._TYPECODE + "} WHERE {"
            + WirecardPaymentConfigurationModel.BASESTORE + "} = ?baseStoreModel";

    private static final String CONFIG_QUERY =
        CONFIG_LIST_QUERY + " AND {" + WirecardPaymentConfigurationModel.PAYMENTMODE + "} = ?paymentModeModel";

    @Override
    public List<WirecardPaymentConfigurationModel> getWirecardPaymentConfigurations(BaseStoreModel baseStoreModel) {
        ServicesUtil.validateParameterNotNull(baseStoreModel, "Base store must not be null");
        return baseStoreModel.getPaymentConfigurations();

    }

    @Override
    public WirecardPaymentConfigurationModel getWirecardPaymentConfiguration(BaseStoreModel baseStoreModel,
                                                                             PaymentModeModel paymentModeModel) {
        ServicesUtil.validateParameterNotNull(baseStoreModel, "Base store must not be null");
        ServicesUtil.validateParameterNotNull(paymentModeModel, "Payment mode must not be null");

        final Map<String, Object> params = new HashMap<>();
        params.put("baseStoreModel", baseStoreModel);
        params.put("paymentModeModel", paymentModeModel);

        final SearchResult<WirecardPaymentConfigurationModel> res = getFlexibleSearchService().search(CONFIG_QUERY, params);

        if (res.getResult() == null || res.getTotalCount() == 0) {
            return null;
        }

        return res.getResult().get(0);
    }
}
