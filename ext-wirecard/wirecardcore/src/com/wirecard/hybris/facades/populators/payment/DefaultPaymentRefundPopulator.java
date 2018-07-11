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

package com.wirecard.hybris.facades.populators.payment;

import com.wirecard.hybris.core.data.types.Money;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.warehousing.returns.service.RefundAmountCalculationService;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

public class DefaultPaymentRefundPopulator implements Populator<ReturnRequestModel, Payment> {

    private RefundAmountCalculationService refundAmountCalculationService;

    private WirecardTransactionService wirecardTransactionService;


    @Override
    public void populate(ReturnRequestModel source, Payment target) throws ConversionException {
        populateRequestedAmount(source, target);
    }

    private void populateRequestedAmount(ReturnRequestModel returnRequest, Payment target) {
        Money money = new Money();
        OrderModel order = returnRequest.getOrder();
        PaymentTransactionModel transaction =
            wirecardTransactionService.lookForCompatibleTransactions(order, order.getPaymentMode().getCode());
        if (transaction != null && transaction.getPaymentProvider() != null) {
            final BigDecimal customRefundAmount = getRefundAmountCalculationService().getCustomRefundAmount(returnRequest);
            BigDecimal amountToRefund;

            if (customRefundAmount != null && customRefundAmount.compareTo(BigDecimal.ZERO) > 0) {
                amountToRefund = customRefundAmount;
            } else {
                amountToRefund = getRefundAmountCalculationService().getOriginalRefundAmount(returnRequest);
            }
            money.setValue(amountToRefund);
        }
        money.setCurrency(returnRequest.getOrder().getCurrency().getIsocode());
        target.setRequestedAmount(money);
    }

    protected RefundAmountCalculationService getRefundAmountCalculationService() {
        return refundAmountCalculationService;
    }

    @Required
    public void setRefundAmountCalculationService(RefundAmountCalculationService refundAmountCalculationService) {
        this.refundAmountCalculationService = refundAmountCalculationService;
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }

}
