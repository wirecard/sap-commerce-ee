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

package com.wirecard.hybris.core.interceptors;

import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.impl.DefaultWirecardPaymentCommandService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import reactor.util.StringUtils;

public class DefaultWirecardAuthenticationValidateInterceptor implements ValidateInterceptor {

    private static final String MODIFY_MESSAGE = "authentication model trying to be modified";
    private static final String NEW_MESSAGE = "new authentication is trying to be saved";
    private static final String VALID_MESSAGE = "authentication credentials: VALID";
    private static final String INVALID_MESSAGE = "authentication credentials: INVALID";
    private static final String SAVED_MESSAGE = "Authentication model saved";
    private static final String NOT_SAVED_MESSAGE = "authentication model NOT saved";
    private static final String ERROR_MESSAGE =
        "The credentials are invalid to connect with Wirecard. Authentication model will not be saved";
    private static final String ERROR_MESSAGE_CREDITOR =
        "The credentials for 'wd-sepadebit' require a creditorId . Authentication model will not be saved";

    private static final Logger LOG = Logger.getLogger(DefaultWirecardAuthenticationValidateInterceptor.class);

    private DefaultWirecardPaymentCommandService wirecardPaymentCommandService;

    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Override
    public void onValidate(Object model, InterceptorContext ctx) throws InterceptorException {

        if (model instanceof WirecardAuthenticationModel) {
            final WirecardAuthenticationModel authenticationModel = (WirecardAuthenticationModel) model;
            if (ctx.isNew(model) || ctx.isModified(model, WirecardAuthenticationModel.USERNAME)
                || ctx.isModified(model, WirecardAuthenticationModel.PASSWORD)) {

                if (!ctx.isNew(authenticationModel)) {
                    registerAuthenticationForRemovalIfNeeded(authenticationModel);
                    LOG.debug(MODIFY_MESSAGE);
                } else {
                    registerAuthenticationForRemovalIfNeeded(authenticationModel);
                    LOG.debug(NEW_MESSAGE);
                }
            }
            if ((ctx.isNew(model) || ctx.isModified(model, WirecardAuthenticationModel.CREDITORID))
                && authenticationModel.getCode().contains("wd-sepadebit") && !StringUtils.hasText(authenticationModel.getCreditorId())) {
                throw new InterceptorException(
                    ERROR_MESSAGE_CREDITOR);
            }
        }
    }

    protected void registerAuthenticationForRemovalIfNeeded(WirecardAuthenticationModel authenticationModel) throws InterceptorException {
        String url = authenticationModel.getBaseUrl() + getWirecardPaymentConfigurationService().getCheckAuthenticationURL();
        int response =
            getWirecardPaymentCommandService().sendTestAuthenticationRequest(url, authenticationModel);

        if (checkResponse(response)) {
            LOG.info(SAVED_MESSAGE);
        } else {
            LOG.info(NOT_SAVED_MESSAGE);
            throw new InterceptorException(
                ERROR_MESSAGE);

        }
    }

    protected boolean checkResponse(int response) {
        boolean validResponse = response == HttpStatus.SC_NOT_FOUND || response == HttpStatus.SC_METHOD_NOT_ALLOWED;
        if (validResponse) {
            LOG.info(VALID_MESSAGE);
        } else {
            LOG.info(INVALID_MESSAGE);
        }
        return validResponse;
    }

    protected DefaultWirecardPaymentCommandService getWirecardPaymentCommandService() {
        return wirecardPaymentCommandService;
    }

    @Required
    public void setWirecardPaymentCommandService(DefaultWirecardPaymentCommandService wirecardPaymentCommandService) {
        this.wirecardPaymentCommandService = wirecardPaymentCommandService;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }
}
