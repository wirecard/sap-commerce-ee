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
import com.wirecard.hybris.core.strategy.DescriptorGenerateStrategy;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.Locale;

public class DefaultWirecardDescriptorGenerateStrategy implements DescriptorGenerateStrategy {

    private static final int MAX_WIDTH = 9;
    private static final int GUID_MAX_WIDTH = 10;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;


    public String getDescriptor(AbstractOrderModel abstractOrderModel) {

        final String shopName = abstractOrderModel.getStore().getName();
        final String webShop = wirecardPaymentConfigurationService.getWebShop();
        final String orderNumber = abstractOrderModel.getGuid();
        String name;

        if (abstractOrderModel instanceof OrderModel) {
            final Locale orderLocale = LocaleUtils.toLocale(((OrderModel) abstractOrderModel).getLanguage().getIsocode());
            name = abstractOrderModel.getStore().getName(orderLocale);
        } else if (shopName != null) {
            name = shopName;
        } else {
            name = webShop;
        }

        return String.format("%s %s", StringUtils.truncate(name, MAX_WIDTH), StringUtils.truncate(orderNumber, GUID_MAX_WIDTH));

    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }
}
