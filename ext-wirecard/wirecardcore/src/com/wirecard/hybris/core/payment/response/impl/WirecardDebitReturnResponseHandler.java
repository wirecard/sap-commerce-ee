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

import com.wirecard.hybris.core.data.types.BankAccount;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.WirecardDebitPaymentInfoModel;
import org.apache.commons.lang3.StringUtils;


public class WirecardDebitReturnResponseHandler extends DefaultOrderResponseHandler<WirecardDebitPaymentInfoModel> {

    @Override
    public void doOrderProcessResponse(AbstractOrderModel item, final PaymentOperationData data) throws WirecardPaymenException {
        setIbanBic(item, data);
    }

    private void setIbanBic(AbstractOrderModel abstractOrder, PaymentOperationData data) {
        if (data.getPayment() != null && hasIbanAndBic(data.getPayment().getBankAccount())
            && abstractOrder.getPaymentInfo() instanceof WirecardDebitPaymentInfoModel) {

            WirecardDebitPaymentInfoModel debitPaymentInfo = (WirecardDebitPaymentInfoModel) abstractOrder.getPaymentInfo();
            debitPaymentInfo.setIban(data.getPayment().getBankAccount().getIban());
            debitPaymentInfo.setBic(data.getPayment().getBankAccount().getBic());
            getModelService().save(debitPaymentInfo);

        }
    }

    private boolean hasIbanAndBic(BankAccount bankAccount) {
        return bankAccount != null && !StringUtils.isEmpty(bankAccount.getIban()) && !StringUtils.isEmpty(bankAccount.getBic());
    }

}
