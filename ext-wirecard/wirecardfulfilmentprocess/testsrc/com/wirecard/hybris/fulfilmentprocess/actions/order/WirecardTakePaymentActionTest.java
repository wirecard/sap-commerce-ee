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

import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.impersonation.ImpersonationService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class WirecardTakePaymentActionTest {

    @InjectMocks
    private WirecardTakePaymentAction wirecardTakePaymentAction;

    @Mock
    private ModelService modelService;
    @Mock
    private ImpersonationService impersonationService;

    private OrderProcessModel orderProcessModel;
    private OrderModel orderModel;

    @Before
    public void setup() throws WirecardPaymenException {

        String code = "CODE";

        orderProcessModel = new OrderProcessModel();
        orderModel = new OrderModel();
        orderModel.setCode(code);
        UserModel userModel = new UserModel();
        userModel.setSessionLanguage(new LanguageModel());
        orderModel.setUser(userModel);
        orderProcessModel.setOrder(orderModel);

        wirecardTakePaymentAction.setModelService(modelService);
    }

    @Test
    public void executeActionTestSuccess() {

        when(impersonationService.executeInContext(Mockito.anyObject(), Mockito.anyObject())).thenReturn(OrderStatus.PAYMENT_CAPTURED);

        AbstractSimpleDecisionAction.Transition transition;

        transition = wirecardTakePaymentAction.executeAction(orderProcessModel);

        //compare both datas
        assertEquals("The transition is not correct", transition, AbstractSimpleDecisionAction.Transition.OK);
    }

    @Test
    public void executeActionTestFailed() {

        AbstractSimpleDecisionAction.Transition transition;

        when(impersonationService.executeInContext(Mockito.anyObject(), Mockito.anyObject())).thenReturn(OrderStatus.CANCELLED);

        transition = wirecardTakePaymentAction.executeAction(orderProcessModel);

        //compare both datas
        assertEquals("The transition should return NOK", transition, AbstractSimpleDecisionAction.Transition.NOK);
    }

}
