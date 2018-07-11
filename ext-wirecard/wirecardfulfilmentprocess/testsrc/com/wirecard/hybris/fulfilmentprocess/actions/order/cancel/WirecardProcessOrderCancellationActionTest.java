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
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.basecommerce.enums.OrderCancelEntryStatus;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelCallbackService;
import de.hybris.platform.ordercancel.OrderCancelService;
import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;
import de.hybris.platform.ordercancel.model.OrderEntryCancelRecordEntryModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractProceduralAction;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WirecardProcessOrderCancellationActionTest {

    @InjectMocks
    private WirecardProcessOrderCancellationAction wirecardProcessOrderCancellationAction;

    @Mock
    private WirecardPOExecutionService wirecardPOExecutionService;

    @Mock
    private OrderCancelService orderCancelService;

    @Mock
    private OrderCancelCallbackService orderCancelCallbackService;

    @Mock
    private ModelService modelService;

    @Mock
    private OrderEntryModel orderEntry;

    @Test(expected = IllegalArgumentException.class)
    public void testCancelNullProcess() throws Exception {
        wirecardProcessOrderCancellationAction.execute(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCancelNullOrder() throws Exception {
        OrderProcessModel process = new OrderProcessModel();
        wirecardProcessOrderCancellationAction.execute(process);
    }

    @Test
    public void testCancelAction() throws Exception {
        OrderModel order = new OrderModel();

        OrderProcessModel process = new OrderProcessModel();
        process.setOrder(order);

        when(orderEntry.getQuantity()).thenReturn(0L);
        when(orderEntry.getOrder()).thenReturn(order);
        when(orderEntry.getQuantityUnallocated()).thenReturn(0L);
        order.setEntries(Collections.singletonList(orderEntry));

        OrderEntryCancelRecordEntryModel orderEntryCancelRecordEntry = new OrderEntryCancelRecordEntryModel();
        orderEntryCancelRecordEntry.setNotes("Notes");
        orderEntryCancelRecordEntry.setCancelRequestQuantity(0);
        orderEntryCancelRecordEntry.setCancelReason(CancelReason.OTHER);
        orderEntryCancelRecordEntry.setOrderEntry(orderEntry);

        OrderCancelRecordEntryModel orderCancelRecordEntry = new OrderCancelRecordEntryModel();
        orderCancelRecordEntry.setOrderEntriesModificationEntries(Collections.singleton(orderEntryCancelRecordEntry));
        orderCancelRecordEntry.setCancelResult(OrderCancelEntryStatus.PARTIAL);
        orderCancelRecordEntry.setNotes("Notes");
        when(orderCancelService.getPendingCancelRecordEntry(order)).thenReturn(orderCancelRecordEntry);

        String transition = wirecardProcessOrderCancellationAction.execute(process);

        assertNotNull("Order status is null, ", order.getStatus());
        assertEquals("Transition does not match", AbstractProceduralAction.Transition.OK.toString(), transition);
    }
}
