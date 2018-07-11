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

import com.wirecard.hybris.core.service.WirecardPaymentModeService;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

public class DefaultWirecardPaymentModeService implements WirecardPaymentModeService {

    private ModelService modelService;
    private PaymentModeService paymentModeService;


    @Override
    public PaymentModeModel getPaymentModeByCode(String code) {

        return getPaymentModeService().getPaymentModeForCode(code);
    }

    @Override
    public void storePaymentMode(AbstractOrderModel abstractOrderModel, PaymentModeModel paymentModeModel) {
        abstractOrderModel.setPaymentStatus(PaymentStatus.NOTPAID);
        abstractOrderModel.setPaymentMode(paymentModeModel);
        modelService.save(abstractOrderModel);

    }

    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }


    protected PaymentModeService getPaymentModeService() {
        return paymentModeService;
    }

    @Required
    public void setPaymentModeService(PaymentModeService paymentModeService) {
        this.paymentModeService = paymentModeService;
    }

}
