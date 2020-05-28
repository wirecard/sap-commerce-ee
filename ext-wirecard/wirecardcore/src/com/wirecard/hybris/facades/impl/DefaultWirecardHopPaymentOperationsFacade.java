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

package com.wirecard.hybris.facades.impl;

import com.wirecard.hybris.core.constants.WirecardPaymentTransactionConstants;
import com.wirecard.hybris.core.converter.xml.PaymentConverter;
import com.wirecard.hybris.core.dao.WirecardOrderModelDao;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.PaymentCommandService;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardPaymentModeService;
import com.wirecard.hybris.core.strategy.PaymentOperationStrategy;
import com.wirecard.hybris.exception.WirecardPaymenException;
import com.wirecard.hybris.exception.constants.WirecardPaymentExceptionConstants;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Base64;
import java.util.Collections;


public class DefaultWirecardHopPaymentOperationsFacade implements WirecardHopPaymentOperationsFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWirecardHopPaymentOperationsFacade.class);

    private static final String RESPONSE_STATUS_LOG_MSG = "response recived for payment method: {} and transaction type: {} in status: {}";
    private static final String START_OPERATION_LOG_MSG = "staring operation of {} {} for cart code {}";

    private PaymentOperationStrategy paymentOperationStrategy;
    private UserService userService;
    private CartService cartService;
    private Converter<AddressData, AddressModel> addressConverter;
    private PaymentConverter paymentConverter;
	private GenericDao<AbstractOrderModel> abstractOrderGenericDao;
    private WirecardOrderModelDao orderModelDao;
    private SessionService sessionService;
    private PaymentCommandService paymentCommandService;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    private WirecardPaymentModeService wirecardPaymentModeService;

    @Override
    public Payment executePaymentOperation(PaymentOperationData data) throws
        WirecardPaymenException {
        Payment response;

        CartModel sessionCart = cartService.getSessionCart();

        LOG.info(START_OPERATION_LOG_MSG,
                 sessionCart.getPaymentMode().getCode(),
                 WirecardPaymentTransactionConstants.AUTHORIZATION,
                 sessionCart.getCode());

        response = getPaymentOperationStrategy().getOperation(sessionCart.getPaymentMode())
                                                .doOperation(sessionCart, data);

        if (response == null) {
            throw new WirecardPaymenException(WirecardPaymentExceptionConstants.NULL_ERROR);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info(RESPONSE_STATUS_LOG_MSG,
                     response.getPaymentMethods().getPaymentMethod().get(0).getName().value(),
                     response.getTransactionType().value(),
                     response.getTransactionState().value());
        }

        return response;
    }

	@Override
	public Payment executePaymentOperation(final String operation, final PaymentOperationData data)
			throws WirecardPaymenException {
		if (getCartService().hasSessionCart()) {
			return executePaymentOperation(operation, data, getCartService().getSessionCart());
		} else {
			final String orderNumber = data.getPayment().getOrderNumber();
			return executePaymentOperation(operation, data, orderNumber);
		}
	}

    @Override
    public Payment executePaymentOperation(String operation, PaymentOperationData data, String orderNumber)
            throws WirecardPaymenException {
        try {
            OrderModel orderModel = getOrderModelDao().getOrderModelByGuid(orderNumber);
            return executePaymentOperation(operation, data, orderModel);
        } catch (ModelNotFoundException ex) {
			LOG.warn("Notification doesn`t match with any order, trying to get Cart: {}", orderNumber);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Notification doesn`t match with any order", ex);
			}
			return executePaymentOperationOnCart(operation, data, orderNumber);
        }
    }

	private Payment executePaymentOperationOnCart(String operation, PaymentOperationData data, String orderNumber)
			throws WirecardPaymenException {
		//WIRE-19: Fallback for cases where the order model cannot be found.
		//This might be necessary if a client loses their session, for example during a PayPal payment.
		//We try to get the cart instead and proceed with the payment normally after casting it into an order.
		//If that also fails, we throw a PaymentException to gracefully logout the client.
		try {
			final CartModel cartModel = getOrderModelDao().getCartModelByGuid(orderNumber);
			final AbstractOrderModel orderModel = (AbstractOrderModel) cartModel;
			return executePaymentOperation(operation, data, orderModel);
		} catch (ModelNotFoundException ex) {
			LOG.warn("Notification doesn`t match with any cart: {}", orderNumber);
			if (LOG.isDebugEnabled()) {
				LOG.debug("Notification doesn`t match with any cart", ex);
			}
			throw new WirecardPaymenException("Model not found for guid " + orderNumber, ex);
		}
	}

    private Payment executePaymentOperation(String operation, PaymentOperationData data, AbstractOrderModel order)
        throws WirecardPaymenException {

        LOG.info(START_OPERATION_LOG_MSG, order.getPaymentMode(), operation, order.getCode());

        Payment response = getPaymentOperationStrategy().getOperation(order.getPaymentMode(), operation)
                                                        .doOperation(order, data);

        if (response == null) {
            throw new WirecardPaymenException(WirecardPaymentExceptionConstants.NULL_ERROR);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info(RESPONSE_STATUS_LOG_MSG,
                     response.getPaymentMethods().getPaymentMethod().get(0).getName().value(),
                     response.getTransactionType().value(),
                     response.getTransactionState().value());
        }

        return response;
    }

    @Override
    public String getPaymentResponseFromWirecard(String paymentCode, String requestId) {

        PaymentModeModel paymentMode = getWirecardPaymentModeService().getPaymentModeByCode(paymentCode);

        String baseUrl = getWirecardPaymentConfigurationService().getBaseURL(paymentCode);
        String restMerchantURL = getWirecardPaymentConfigurationService().getCheckAuthenticationURL();
        WirecardAuthenticationModel authentication =
            getWirecardPaymentConfigurationService().getConfiguration(paymentMode).getAuthentication();
        String searchURL = getWirecardPaymentConfigurationService().getSearchURL();
        StringBuilder stringGetURL =
            new StringBuilder().append(baseUrl)
                               .append(restMerchantURL)
                               .append(authentication.getMaid())
                               .append(searchURL)
                               .append(requestId);

        return paymentCommandService.sendSearchRequest(stringGetURL.toString(), authentication);
    }

    @Override
    public String getIdealBic() {
        return sessionService.getCurrentSession().getAttribute("ideal-bic");
    }

    @Override
    public void setIdealBic(String bic) {
        sessionService.getCurrentSession().setAttribute("ideal-bic", bic);
    }

    @Override
    public boolean isSaveInAccount() {
        return BooleanUtils.isTrue(sessionService.getCurrentSession().getAttribute("isSaveInAccount"));
    }

    @Override
    public void setIsSaveInAccount(boolean isSaveInAccount) {
        sessionService.getCurrentSession().setAttribute("isSaveInAccount", isSaveInAccount);
    }

    @Override
    public boolean isSavedCC() {
        return BooleanUtils.isTrue(sessionService.getCurrentSession().getAttribute("isSavedCC"));
    }

    @Override
    public void setIsSavedCC(boolean isSavedCC) {
        sessionService.getCurrentSession().setAttribute("isSavedCC", isSavedCC);
    }


    @Override
    public Payment parseMessage(String content, boolean isEncoded, boolean checkSignature) throws WirecardPaymenException {
        if (isEncoded) {
            return getPaymentConverter().convertXMLToData(decodeBase64(content), checkSignature);
        } else {
            return getPaymentConverter().convertXMLToData(content);
        }
    }

    /**
     * Decode string using Base64 decoder
     * If this can not be done, the content will be returned without decoding
     *
     * @param content
     *     text to decode
     * @return decoded text
     */
    private String decodeBase64(String content) {
        String xml;
        try {
            xml = new String(Base64.getDecoder().decode(content));
        } catch (IllegalArgumentException ex) { //NOSONAR
            xml = content;
            if (LOG.isDebugEnabled()) {
                LOG.debug(ex.getMessage());
            }
        }
        return xml;
    }

    protected PaymentOperationStrategy getPaymentOperationStrategy() {
        return paymentOperationStrategy;
    }

    @Required
    public void setPaymentOperationStrategy(PaymentOperationStrategy paymentOperationStrategy) {
        this.paymentOperationStrategy = paymentOperationStrategy;
    }

    protected UserService getUserService() {
        return userService;
    }

    @Required
    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    protected CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }

    protected Converter<AddressData, AddressModel> getAddressConverter() {
        return addressConverter;
    }

    @Required
    public void setAddressConverter(Converter<AddressData, AddressModel> addressConverter) {
        this.addressConverter = addressConverter;
    }

    protected PaymentConverter getPaymentConverter() {
        return paymentConverter;
    }

    @Required
    public void setPaymentConverter(PaymentConverter paymentConverter) {
        this.paymentConverter = paymentConverter;
    }

    protected WirecardOrderModelDao getOrderModelDao() {
        return orderModelDao;
    }

    @Required
    public void setOrderModelDao(WirecardOrderModelDao orderModelDao) {
        this.orderModelDao = orderModelDao;
    }

    protected PaymentCommandService getPaymentCommandService() {
        return paymentCommandService;
    }

    @Required
    public void setPaymentCommandService(PaymentCommandService paymentCommandService) {
        this.paymentCommandService = paymentCommandService;
    }

    protected SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }

    protected WirecardPaymentModeService getWirecardPaymentModeService() {
        return wirecardPaymentModeService;
    }

    @Required
    public void setWirecardPaymentModeService(WirecardPaymentModeService wirecardPaymentModeService) {
        this.wirecardPaymentModeService = wirecardPaymentModeService;
    }
	
	@Required
	public void setAbstractOrderGenericDao(GenericDao<AbstractOrderModel> abstractOrderGenericDao) {
		this.abstractOrderGenericDao = abstractOrderGenericDao;
	}
}
