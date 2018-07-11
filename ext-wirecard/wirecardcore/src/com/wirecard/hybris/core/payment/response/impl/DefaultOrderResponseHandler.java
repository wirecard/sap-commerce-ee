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
package com.wirecard.hybris.core.payment.response.impl;

import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public class DefaultOrderResponseHandler<T extends PaymentInfoModel> extends AbstractResponseHandler<AbstractOrderModel> {

    private WirecardTransactionService wirecardTransactionService;
    private Class<T> paymentInfoClass;
    private Boolean needsRecalculation;

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractOrderModel getOrder(AbstractOrderModel model) {
        return model;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTransactionAmount(AbstractOrderModel item) {
        BigDecimal amount = BigDecimal.valueOf(item.getTotalPrice());
        amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        return amount;
    }

    @Override
    public final void doProcessResponse(final AbstractOrderModel abstractOrderModel, final PaymentOperationData data)
        throws WirecardPaymenException {

        if (getPaymentInfoClass() != null) {
            createPaymentInfo(abstractOrderModel, getPaymentInfoClass(), data);
        }

        doOrderProcessResponse(abstractOrderModel, data);

        if (BooleanUtils.isTrue(getNeedsRecalculation())) {
            calculateCart(abstractOrderModel);
        }
    }

    protected void doOrderProcessResponse(AbstractOrderModel abstractOrderModel, PaymentOperationData data) throws WirecardPaymenException {
        // does nothing by default
    }

    /**
     * With this method we create the payment info of abstract order model
     *
     * @param abstractOrder
     *     the order
     */
    private void createPaymentInfo(final AbstractOrderModel abstractOrder,
                                   Class<T> clazz, PaymentOperationData data) {
        // We create a PaymentInfo that we will use later to extract data and store it in the cart.
        PaymentModeModel paymentModeModel = abstractOrder.getPaymentMode();
        final T wirecardPaymentInfoModel = getModelService().create(clazz);
        wirecardPaymentInfoModel.setCode(abstractOrder.getUser().getUid() + "_" + UUID.randomUUID());
        wirecardPaymentInfoModel.setBillingAddress(abstractOrder.getPaymentAddress());
        abstractOrder.setPaymentMode(paymentModeModel);
        wirecardPaymentInfoModel.setUser(abstractOrder.getUser());
        abstractOrder.setPaymentInfo(wirecardPaymentInfoModel);
        fillPaymentInfoForPaymentMode(wirecardPaymentInfoModel, data);
        abstractOrder.setPaymentStatus(PaymentStatus.PENDING);
        getModelService().saveAll(wirecardPaymentInfoModel, abstractOrder);
    }

    protected void fillPaymentInfoForPaymentMode(PaymentInfoModel wirecardPaymentInfoModel, PaymentOperationData data) {
        // additional steps before saving wirecardPaymentInfoModel
    }

    private void calculateCart(final AbstractOrderModel abstractOrderModel) {
        getWirecardTransactionService().calculateCart(abstractOrderModel);
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }

    protected Class<T> getPaymentInfoClass() {
        return paymentInfoClass;
    }

    public void setPaymentInfoClass(Class<T> paymentInfoClass) {
        this.paymentInfoClass = paymentInfoClass;
    }

    protected Boolean getNeedsRecalculation() {
        return needsRecalculation;
    }

    public void setNeedsRecalculation(Boolean needsRecalculation) {
        this.needsRecalculation = needsRecalculation;
    }
}
