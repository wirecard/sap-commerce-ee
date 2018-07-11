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

package com.wirecard.hybris.core.service.impl;

import com.wirecard.hybris.core.dao.WirecardAuthenticationDao;
import com.wirecard.hybris.core.dao.WirecardPaymentConfigurationDao;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.core.payment.filter.PaymentConfigurationFilter;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardPaymentModeService;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultWirecardPaymentConfigurationService implements WirecardPaymentConfigurationService {

    private static final String WIRECARD_WEBSHOP = "wirecard.webshop";
    private static final String HOP_SUCCESS_URL = "hop.success.url";
    private static final String HOP_CANCEL_URL = "hop.cancel.url";
    private static final String SUCCESS_URL_TEMPLATE = "wirecard.%s.success.url";
    private static final String CANCEL_URL_TEMPLATE = "wirecard.%s.cancel.url";
    private static final String HOP_PLACEORDER_URL = "hop.placeorder.url";
    private static final String HOP_TERM_URL = "hop.term.url";
    private static final String WIRECARD_NOTIFICATION_URL = "wirecard.notification.url";
    private static final String WIRECARD_CHECK_AUTH_URL = "wirecard.authentication.check.url";
    private static final String WIRECARD_SEARCH_URL = "wirecard.search.url";
    private static final String VOID_PROMOTIONS = "[]";

    private WirecardPaymentConfigurationDao wirecardPaymentConfigurationDao;
    private WirecardAuthenticationDao wirecardAuthenticationDao;
    private BaseStoreService baseStoreService;
    private CartService cartService;
    private ConfigurationService configurationService;
    private BaseSiteService baseSiteService;
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
    private WirecardPaymentService wirecardPaymentService;
    private WirecardTransactionService wirecardTransactionService;
    private WirecardPaymentModeService wirecardPaymentModeService;

    private List<PaymentConfigurationFilter> paymentConfigurationFilters;

    public static <T> Predicate<T> distinct(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * Method to retrieve all configurations from the current Base Store
     *
     * @return the list of configurations
     */
    protected List<WirecardPaymentConfigurationModel> getAllConfigurationsForBaseStore() {
        return wirecardPaymentConfigurationDao.getWirecardPaymentConfigurations(baseStoreService.getCurrentBaseStore());
    }

    /**
     * Method to retrieve all configurations valid for the current cart
     *
     * @return the list of valid configurations
     */
    protected List<WirecardPaymentConfigurationModel> getAllConfigurationsForCurrentCart() {
        CartModel sessionCart = cartService.getSessionCart();
        return getAllConfigurationsForBaseStore().stream()
                                                 .filter(configuration -> isValidPaymentConfiguration(configuration, sessionCart))
                                                 .collect(Collectors.toList());
    }

    /**
     * Method to check if the configuration is valid for the current order
     *
     * @param paymentConfiguration
     *     Current payment configuration
     * @param order
     *     The order
     */
    protected boolean isValidPaymentConfiguration(WirecardPaymentConfigurationModel paymentConfiguration, AbstractOrderModel order) {
        return getPaymentConfigurationFilters().stream().allMatch(filter -> filter.isValid(paymentConfiguration, order));
    }

    @Override
    public List<PaymentModeModel> getAllAllowedPaymentModes() {

        List<WirecardPaymentConfigurationModel> visibleConfigs = getAllConfigurationsForCurrentCart();
        return visibleConfigs.stream()
                             .map(WirecardPaymentConfigurationModel::getPaymentMode)
                             .filter(PaymentModeModel::getActive)
                             .filter(pay -> pay.getTransactionType() != null)
                             // sanity check to only allow one credit cardo
                             .filter(distinct(PaymentModeModel::getPaymentAlias))
                             .collect(Collectors.toList());

    }

    @Override
    public List<CountryModel> getSupportedCountries(PaymentModeModel paymentModeModel) {

        List<WirecardPaymentConfigurationModel> visibleConfigs = getAllConfigurationsForCurrentCart();
        return new ArrayList<>(visibleConfigs.stream()
                                             .filter(c -> c.getBaseStore().equals(baseStoreService.getCurrentBaseStore())
                                                 && c.getPaymentMode().equals(paymentModeModel))
                                             .findFirst()
                                             .map(WirecardPaymentConfigurationModel::getBillingCountries)
                                             .orElseGet(Collections::emptyList));


    }

    @Override
    public Boolean getSameAddress(PaymentModeModel paymentModeModel) {

        List<WirecardPaymentConfigurationModel> visibleConfigs = getAllConfigurationsForCurrentCart();
        return visibleConfigs.stream()
                             .filter(c -> c.getBaseStore().equals(baseStoreService.getCurrentBaseStore())
                                 && c.getPaymentMode().equals(paymentModeModel))
                             .findFirst()
                             .map(WirecardPaymentConfigurationModel::getSameAddress)
                             .orElse(false);

    }

    @Override
    public WirecardPaymentConfigurationModel getConfiguration(PaymentModeModel paymentModeModel) {

        return wirecardPaymentConfigurationDao.getWirecardPaymentConfiguration(baseStoreService.getCurrentBaseStore(), paymentModeModel);
    }

    @Override
    public WirecardPaymentConfigurationModel getConfiguration(BaseStoreModel baseStoreModel, PaymentModeModel paymentModeModel) {

        return wirecardPaymentConfigurationDao.getWirecardPaymentConfiguration(baseStoreModel, paymentModeModel);
    }

    @Override
    public WirecardAuthenticationModel getAuthentication(AbstractOrderModel abstractOrderModel) {
        PaymentTransactionModel paymentTransactionModel = wirecardTransactionService.getPaymentTransaction(abstractOrderModel);
        WirecardAuthenticationModel authentication = null;

        if (paymentTransactionModel != null && paymentTransactionModel.getAuthentication() != null) {
            authentication = paymentTransactionModel.getAuthentication();
        }

        return authentication;
    }


    @Override
    public String getWirecardParameter(final String wirecardParameter) {
        return getWirecardParameter(wirecardParameter, null);
    }

    @Override
    public String getWirecardParameter(final String wirecardParameter, String defaultValue) {
        return configurationService.getConfiguration().getString(wirecardParameter, defaultValue);
    }

    @Override
    public String getBaseURL(String paymentMethodCode) {
        return wirecardAuthenticationDao.getWirecardPaymentBaseURL(baseStoreService.getCurrentBaseStore(),
                                                                   wirecardPaymentModeService.getPaymentModeByCode(paymentMethodCode));
    }

    @Override
    public String getWebShop() {

        return getWirecardParameter(WIRECARD_WEBSHOP);

    }


    @Override
    public String getSuccesURL(PaymentModeModel paymentMode) {
        return getURL(paymentMode, SUCCESS_URL_TEMPLATE, HOP_SUCCESS_URL);
    }

    @Override
    public String getCancelURL(PaymentModeModel paymentMode) {
        return getURL(paymentMode, CANCEL_URL_TEMPLATE, HOP_CANCEL_URL);
    }

    protected String getURL(PaymentModeModel paymentMode, String template, String fallback) {

        String code;
        if (paymentMode == null) {
            code = fallback;
        } else {
            code = String.format(template, paymentMode.getPaymentAlias());
        }
        String fallbackUrl = getWirecardParameter(fallback);
        String url = getWirecardParameter(code, fallbackUrl);
        return getFullResponseUrl(url, true);
    }

    @Override
    public String getPlaceOrderURL() {
        return getFullResponseUrl(getWirecardParameter(HOP_PLACEORDER_URL), true);
    }


    @Override
    public String getAcsTermURL() {
        return getFullResponseUrl(getWirecardParameter(HOP_TERM_URL), true);
    }


    /**
     * Method to get the full response URL
     *
     * @param responseUrl
     *     Wirecard partial URL
     * @param isSecure
     *     boolean to check if the URL is secure
     * @return the full response URL
     */
    protected String getFullResponseUrl(final String responseUrl, final boolean isSecure) {
        return getFullResponseUrl(responseUrl, null, isSecure);
    }

    /**
     * * Method to get the full response URL
     *
     * @param responseUrl
     *     Wirecard partial URL
     * @param abstractOrderModel
     *     the order
     * @param isSecure
     *     boolean to check if the URL is secure
     * @return the full response URL
     */
    protected String getFullResponseUrl(final String responseUrl, AbstractOrderModel abstractOrderModel, final boolean isSecure) {
        BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
        if (currentBaseSite == null && abstractOrderModel != null) {
            currentBaseSite = abstractOrderModel.getSite();
        }

        if (currentBaseSite != null) {
            final String fullResponseUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(currentBaseSite, isSecure, responseUrl);
            if (fullResponseUrl == null) {
                return "";
            }
            return fullResponseUrl;
        }
        return null;
    }

    @Override
    public String getNotificationsURL(AbstractOrderModel abstractOrderModel) {
        return getFullResponseUrl(getWirecardParameter(WIRECARD_NOTIFICATION_URL), abstractOrderModel, true);
    }

    @Override
    public String getCheckAuthenticationURL() {
        return getWirecardParameter(WIRECARD_CHECK_AUTH_URL);
    }

    @Override
    public String getSearchURL() {
        return getWirecardParameter(WIRECARD_SEARCH_URL);
    }

    @Override
    public boolean hasNoDiscounts(AbstractOrderModel orderModel) {
        return !containsDiscountData(orderModel.getGlobalDiscountValuesInternal())
            && orderModel.getEntries().stream().noneMatch(entry -> containsDiscountData(entry.getDiscountValuesInternal()));
    }

    private boolean containsDiscountData(String discountValuesInternal) {
        return discountValuesInternal != null && !VOID_PROMOTIONS.equals(discountValuesInternal);
    }

    protected WirecardPaymentConfigurationDao getWirecardPaymentConfigurationDao() {
        return wirecardPaymentConfigurationDao;
    }

    @Required
    public void setWirecardPaymentConfigurationDao(WirecardPaymentConfigurationDao wirecardPaymentConfigurationDao) {
        this.wirecardPaymentConfigurationDao = wirecardPaymentConfigurationDao;
    }

    protected WirecardAuthenticationDao getWirecardAuthenticationDao() {
        return wirecardAuthenticationDao;
    }

    @Required
    public void setWirecardAuthenticationDao(WirecardAuthenticationDao wirecardAuthenticationDao) {
        this.wirecardAuthenticationDao = wirecardAuthenticationDao;
    }

    protected BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    protected CartService getCartService() {
        return cartService;
    }

    @Required
    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    protected ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService() {
        return siteBaseUrlResolutionService;
    }

    @Required
    public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }

    protected WirecardPaymentModeService getWirecardPaymentModeService() {
        return wirecardPaymentModeService;
    }

    @Required
    public void setWirecardPaymentModeService(WirecardPaymentModeService wirecardPaymentModeService) {
        this.wirecardPaymentModeService = wirecardPaymentModeService;
    }

    public WirecardPaymentService getWirecardPaymentService() {
        return wirecardPaymentService;
    }

    @Required
    public void setWirecardPaymentService(WirecardPaymentService wirecardPaymentService) {
        this.wirecardPaymentService = wirecardPaymentService;
    }

    protected List<PaymentConfigurationFilter> getPaymentConfigurationFilters() {
        return paymentConfigurationFilters;
    }

    @Required
    public void setPaymentConfigurationFilters(List<PaymentConfigurationFilter> paymentConfigurationFilters) {
        this.paymentConfigurationFilters = paymentConfigurationFilters;
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }


}
