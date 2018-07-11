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

package com.wirecard.hybris.fulfilmentprocess.actions.refund;

import com.wirecard.hybris.core.service.WirecardPOExecutionService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
public class WirecardCaptureRefundActionTest {

    private WirecardCaptureRefundAction wirecardCaptureRefundAction;

    @Mock
    private WirecardPOExecutionService wirecardPOExecutionService;

    private ReturnProcessModel returnProcessModel;
    private ReturnRequestModel returnRequestModel;

    @Mock
    private ModelService modelService;

    private OrderModel orderModel;

    @Before
    public void setup() throws WirecardPaymenException {

        MockitoAnnotations.initMocks(this);

        wirecardCaptureRefundAction = new WirecardCaptureRefundAction();
        wirecardCaptureRefundAction.setWirecardPOExecutionService(wirecardPOExecutionService);

        String code = "CODE";

        returnRequestModel = new ReturnRequestModel();
        orderModel = new OrderModel();
        orderModel.setCode(code);
        returnRequestModel.setOrder(orderModel);

        returnProcessModel = new ReturnProcessModel();
        returnProcessModel.setReturnRequest(returnRequestModel);

        when(wirecardPOExecutionService.executePaymentRefundOperation(returnRequestModel, null))
                .thenReturn(ReturnStatus.PAYMENT_REVERSED);

        wirecardCaptureRefundAction.setModelService(modelService);
    }

    @Test
    public void executeActionTestSuccess() {

        AbstractSimpleDecisionAction.Transition transition;

        transition = wirecardCaptureRefundAction.executeAction(returnProcessModel);

        //compare both datas
        assertEquals("The transition is not correct", AbstractSimpleDecisionAction.Transition.OK, transition);
    }

    @Test
    public void executeActionTestFailed() {

        AbstractSimpleDecisionAction.Transition transition;

        when(wirecardPOExecutionService.executePaymentRefundOperation(returnRequestModel, null))
                .thenReturn(ReturnStatus.PAYMENT_REVERSAL_FAILED);

        transition = wirecardCaptureRefundAction.executeAction(returnProcessModel);

        //compare both datas
        assertEquals("The transition should return NOK", AbstractSimpleDecisionAction.Transition.NOK, transition);
    }

}
