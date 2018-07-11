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
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Part of the refund process that returns the money to the customer.
 */
public class WirecardCaptureRefundAction extends AbstractSimpleDecisionAction<ReturnProcessModel> {

    private static final String START_MESSAGE = "Starting refund capture payment action ...";
    private static final String STATUS_MESSAGE = "Capture refund payment action on return request {} ended with status {}";
    private static final String PROCESS_MESSAGE = "Process: {} in step {}";
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WirecardCaptureRefundAction.class);
    private WirecardPOExecutionService wirecardPOExecutionService;


    @Override
    public Transition executeAction(final ReturnProcessModel process) {

        LOG.info(START_MESSAGE);
        LOG.info(PROCESS_MESSAGE, process.getCode(), getClass().getSimpleName());
        Transition result = Transition.NOK;

        final ReturnRequestModel returnRequestModel = process.getReturnRequest();
        ReturnStatus returnStatus = wirecardPOExecutionService.executePaymentRefundOperation(returnRequestModel, null);
        if (ReturnStatus.PAYMENT_REVERSED.getCode().equals(returnStatus.getCode())) {
            result = Transition.OK;
        }
        LOG.info(STATUS_MESSAGE, returnRequestModel.getCode(), returnStatus.getCode());

        return result;
    }

    protected WirecardPOExecutionService getWirecardPOExecutionService() {
        return wirecardPOExecutionService;
    }

    @Required
    public void setWirecardPOExecutionService(WirecardPOExecutionService wirecardPOExecutionService) {
        this.wirecardPOExecutionService = wirecardPOExecutionService;
    }

}
