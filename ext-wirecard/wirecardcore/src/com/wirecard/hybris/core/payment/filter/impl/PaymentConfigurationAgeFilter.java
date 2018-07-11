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
import com.wirecard.hybris.core.payment.filter.PaymentConfigurationFilter;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.Calendar;
import java.util.Date;

public class PaymentConfigurationAgeFilter implements PaymentConfigurationFilter {

    private static final int LAST_HOUR_OF_DAY = 23;
    private static final int LAST_MINUTE = 59;

    @Override
    public boolean isValid(WirecardPaymentConfigurationModel configuration, AbstractOrderModel order) {
        boolean valid = true;

        if (configuration != null && order != null) {
            Short age = configuration.getYears();
            if (age != null && order.getDeliveryAddress() != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR, LAST_HOUR_OF_DAY);
                calendar.set(Calendar.MINUTE, LAST_MINUTE);

                Date dateOfBirth;
                if (order.getDeliveryAddress().getDateOfBirth() != null) {
                    dateOfBirth = order.getDeliveryAddress().getDateOfBirth();
                } else{
                    dateOfBirth = calendar.getTime();
                }

                calendar.add(Calendar.YEAR, -age);
                valid = calendar.getTime().compareTo(dateOfBirth) >= 0;
            }
        }

        return valid;
    }
}
