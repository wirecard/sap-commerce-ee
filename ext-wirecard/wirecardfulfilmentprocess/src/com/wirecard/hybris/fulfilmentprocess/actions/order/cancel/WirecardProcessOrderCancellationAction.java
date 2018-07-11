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

package com.wirecard.hybris.fulfilmentprocess.actions.order.cancel;

import com.wirecard.hybris.core.service.WirecardPOExecutionService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelResponse;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.yacceleratorordermanagement.actions.order.cancel.ProcessOrderCancellationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public class WirecardProcessOrderCancellationAction extends ProcessOrderCancellationAction {

    private static final String START_MESSAGE = "Starting cancellation payment action ...";
    private static final String STATUS_MESSAGE = "Cancellation on order {} ended with status {}";
    private static final String PROCESS_MESSAGE = "Process: {} in step {}";

    private static final Logger LOG = LoggerFactory.getLogger(WirecardProcessOrderCancellationAction.class);
    private WirecardPOExecutionService wirecardPOExecutionService;

    @Override
    public String execute(OrderProcessModel process) throws Exception {

        LOG.info(START_MESSAGE);
        ServicesUtil.validateParameterNotNullStandardMessage("process", process);
        LOG.info(PROCESS_MESSAGE, process.getCode(), getClass().getSimpleName());

        final OrderModel order = process.getOrder();
        ServicesUtil.validateParameterNotNullStandardMessage("order", order);

        final OrderCancelRecordEntryModel orderCancelRecordEntryModel = getOrderCancelService().getPendingCancelRecordEntry(order);
        final OrderCancelResponse orderCancelResponse = createOrderCancelResponseFromCancelRecordEntry(order,
                                                                                                       orderCancelRecordEntryModel);
        getWirecardPOExecutionService().executeAuthorizationCancelOperation(order);

        getOrderCancelCallbackService().onOrderCancelResponse(orderCancelResponse);

        OrderStatus orderStatus = getUpdatedOrderStatus(order);
        order.setStatus(orderStatus);
        getModelService().save(order);

        LOG.info(STATUS_MESSAGE, order.getCode(), orderStatus.getCode());

        //Restricting Re-sourcing when an ON_HOLD order gets cancelled
        return calculateTransitionResult(order);
    }

    private String calculateTransitionResult(OrderModel order) {
        if (!OrderStatus.ON_HOLD.equals(order.getStatus()) && existsUnallocatedEntry(order)) {
            return Transition.SOURCING.toString();
        } else if (existsPendingEntry(order)) {
            return Transition.WAIT.toString();
        } else {
            return Transition.OK.toString();
        }

    }

    private OrderStatus getUpdatedOrderStatus(OrderModel order) {
        OrderStatus orderStatus = order.getStatus();
        if (allEntriesCancelled(order)) {
            orderStatus = OrderStatus.CANCELLED;
        } else if (!OrderStatus.ON_HOLD.equals(order.getStatus())) {
            if (existsUnallocatedEntry(order)) {
                orderStatus = OrderStatus.SUSPENDED;
            } else {
                orderStatus = OrderStatus.READY;
            }
        }
        return orderStatus;
    }

    private boolean allEntriesCancelled(OrderModel order) {
        return order.getEntries().stream()
                    .allMatch(entry -> entry.getQuantity() != null && entry.getQuantity() == 0);
    }

    private boolean existsUnallocatedEntry(OrderModel order) {
        return order.getEntries().stream()
                    .anyMatch(entry -> ((OrderEntryModel) entry).getQuantityUnallocated() > 0);

    }

    private boolean existsPendingEntry(OrderModel order) {
        return order.getEntries().stream()
                    .anyMatch(entry -> ((OrderEntryModel) entry).getQuantityPending() > 0);
    }

    protected WirecardPOExecutionService getWirecardPOExecutionService() {
        return wirecardPOExecutionService;
    }

    @Required
    public void setWirecardPOExecutionService(WirecardPOExecutionService wirecardPOExecutionService) {
        this.wirecardPOExecutionService = wirecardPOExecutionService;
    }

}
