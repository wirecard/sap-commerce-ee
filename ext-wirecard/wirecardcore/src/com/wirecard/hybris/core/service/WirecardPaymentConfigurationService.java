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

package com.wirecard.hybris.core.service;

import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

public interface WirecardPaymentConfigurationService {

    /**
     * Filter payment configurations for active payment methods for current cart
     *
     * @return a list of WirecardConfigurationModel to show on store
     */
    List<PaymentModeModel> getAllAllowedPaymentModes();

    /**
     * Get the Supported Countries for current payment mode
     *
     * @return a list of CountryModel to show on store
     */
    List<CountryModel> getSupportedCountries(PaymentModeModel paymentModeModel);

    /**
     * This method returns if the method payment needs the
     * same address for billing and shipping address
     *
     * @param paymentModeModel
     *     the payment mode that want to know if needs the same address
     * @return true if needs same address or false if not needed
     */
    Boolean getSameAddress(PaymentModeModel paymentModeModel);

    /**
     * Get the configuration for current payment mode
     *
     * @param paymentModeModel
     *     current selected payment mode
     * @return WirecardPaymentConfigurationModel containing credentials to use for payments
     */
    WirecardPaymentConfigurationModel getConfiguration(PaymentModeModel paymentModeModel);

    /**
     * Get the configuration for current payment mode
     *
     * @param paymentModeModel
     *     current selected payment mode
     * @param baseStoreModel
     *     current Base Store
     * @return WirecardPaymentConfigurationModel containing credentials to use for payments
     */
    WirecardPaymentConfigurationModel getConfiguration(BaseStoreModel baseStoreModel, PaymentModeModel paymentModeModel);

    /**
     * Get the authentication for abstractOrderModel
     *
     * @param abstractOrderModel
     *     the order
     * @return WirecardAuthenticationModel containing credentials to use for payments null if not stored
     */
    WirecardAuthenticationModel getAuthentication(AbstractOrderModel abstractOrderModel);


    /**
     * This method returns the default URL from wirecard use to store
     * in the payment method to make calls to the server
     *
     * @param paymentMethodCode
     *     the code of the current payment method
     * @return default URL from wirecard server to make calls
     */
    String getBaseURL(String paymentMethodCode);

    /**
     * This method returns a wirecard parameter
     *
     * @param wirecardParameter
     *     the key of parameter
     * @return the parameter
     */
    String getWirecardParameter(final String wirecardParameter);

    /**
     * This method returns a wirecard parameter
     *
     * @param wirecardParameter
     *     the key of the parameter
     * @param defaultValue
     *     The default value if no value was found under given key
     * @return the parameter or the default value if no parameter was found
     */
    String getWirecardParameter(String wirecardParameter, String defaultValue);

    /**
     * Returns the wirecard default webshop name
     *
     * @return the default wirecard wirecard
     */
    String getWebShop();

    /**
     * This method returns the URL where wirecard should redirect customers when payments are success
     *
     * @param paymentModeModel
     *     The payement mode for which the success url should be returned
     * @return success url for redirect customers
     */
    String getSuccesURL(PaymentModeModel paymentModeModel);

    /**
     * This method returns the URL where wirecard should redirect customers when they cancel payments
     *
     * @param paymentModeModel
     *     The payement mode for which the cancel url should be returned
     * @return cancel URL for redirect customers
     */
    String getCancelURL(PaymentModeModel paymentModeModel);

    /**
     * This method returns the URL where wirecard should redirect
     * customers to place the order
     *
     * @return success url for redirect customers
     */
    String getPlaceOrderURL();


    /**
     * This method returns the URL where ACS should return
     *
     * @return URL where ACS should return
     */
    String getAcsTermURL();

    /**
     * This method returns the URL where wirecard should redirect notifications
     *
     * @param abstractOrderModel
     *     The order
     * @return notifications URL
     */
    String getNotificationsURL(AbstractOrderModel abstractOrderModel);

    /**
     * This method returns the URL where wirecard should redirect to check authentication
     *
     * @return authentication check URL
     */
    String getCheckAuthenticationURL();

    /**
     * gives the wirecard url for search
     *
     * @return the Wirecard Search Url
     */
    String getSearchURL();

    /**
     * Check if and order has discounts
     *
     * @param orderModel
     *     Order to be checked
     * @return true if has no discounts, false otherwise
     */
    boolean hasNoDiscounts(AbstractOrderModel orderModel);
}


