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

package com.wirecard.hybris.fulfilmentprocess.actions.order;

import com.wirecard.hybris.core.service.WirecardPOExecutionService;
import de.hybris.platform.commerceservices.impersonation.ImpersonationContext;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * The TakePayment step captures the payment transaction.
 */
public class WirecardTakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel> {

    private static final String START_MESSAGE = "Starting take payment action on order {}";
    private static final String STATUS_MESSAGE = "Take payment action on order {} ended with status {}";
    private static final String NULL_ERROR_MESSAGE = "Order Status is Null, take payment action has been interrupted";
    private static final Logger LOG = LoggerFactory.getLogger(WirecardTakePaymentAction.class);

    private WirecardPOExecutionService wirecardPOExecutionService;

    private ImpersonationService impersonationService;

    private I18NService i18NService;


    @Override
    public Transition executeAction(final OrderProcessModel process) {
        final OrderModel order = process.getOrder();

        LOG.info(START_MESSAGE, order.getCode());

        ImpersonationContext context = new ImpersonationContext();
        context.setOrder(order);
        context.setUser(order.getUser());
        context.setLanguage(order.getUser().getSessionLanguage());
        context.setCurrency(order.getCurrency());
        context.setSite(order.getSite());

        OrderStatus orderStatus = impersonationService.executeInContext(
            context,
            (ImpersonationService.Executor<OrderStatus, ImpersonationService.Nothing>) () -> {
                i18NService.setLocalizationFallbackEnabled(true);
                return wirecardPOExecutionService.executePaymentCaptureOperation(order, null);
            });

        Transition result = Transition.NOK;

        if (orderStatus != null && orderStatus.getCode() != null) {
            if (OrderStatus.PAYMENT_CAPTURED.getCode().equals(orderStatus.getCode())) {
                result = Transition.OK;
                LOG.info(STATUS_MESSAGE, order.getCode(), orderStatus.getCode());

            }
        } else {
            LOG.error(NULL_ERROR_MESSAGE);
        }
        setOrderStatus(order, orderStatus);

        return result;
    }

    protected WirecardPOExecutionService getWirecardPOExecutionService() {
        return wirecardPOExecutionService;
    }

    @Required
    public void setWirecardPOExecutionService(WirecardPOExecutionService wirecardPaymentOperationExecution) {
        this.wirecardPOExecutionService = wirecardPaymentOperationExecution;
    }

    protected ImpersonationService getImpersonationService() {
        return impersonationService;
    }

    @Required
    public void setImpersonationService(ImpersonationService impersonationService) {
        this.impersonationService = impersonationService;
    }

    protected I18NService getI18NService() {
        return i18NService;
    }

    @Required
    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }
}
