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
import com.wirecard.hybris.core.data.types.Mandate;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.SepaPaymentInfoModel;
import org.apache.commons.lang3.StringUtils;

public class WirecardSepaAuthorizationResponseHandler extends DefaultOrderResponseHandler<SepaPaymentInfoModel> {

    @Override
    public void doOrderProcessResponse(final AbstractOrderModel abstractOrderModel, final PaymentOperationData data)
        throws WirecardPaymenException {

        storeSepaData(abstractOrderModel, data);
    }

    private void storeSepaData(AbstractOrderModel abstractOrderModel, PaymentOperationData data) {

        Payment payment = data.getPayment();
        if (payment != null) {
            SepaPaymentInfoModel paymentInfoModel = (SepaPaymentInfoModel) abstractOrderModel.getPaymentInfo();
            setIbanBic(paymentInfoModel, payment.getBankAccount());
            setMandate(paymentInfoModel, data.getPayment().getMandate());
            setCreditorId(paymentInfoModel, payment);
            setAccountOwner(paymentInfoModel, data.getSepaAccountOwner());
            getModelService().save(abstractOrderModel.getPaymentInfo());
        }
    }

    private void setMandate(SepaPaymentInfoModel paymentInfoModel, Mandate mandate) {
        if (mandate != null) {
            paymentInfoModel.setMandateId(mandate.getMandateId());
            paymentInfoModel.setMandateSignedDate(mandate.getSignedDate());
        }
    }

    private void setCreditorId(SepaPaymentInfoModel paymentInfoModel, Payment payment) {
        paymentInfoModel.setCreditorId(payment.getCreditorId());
        paymentInfoModel.setDueDate(payment.getDueDate());
    }

    private void setAccountOwner(SepaPaymentInfoModel paymentInfoModel, String accountOwner) {

        if (StringUtils.isNotEmpty(accountOwner)) {
            paymentInfoModel.setAccountOwner(accountOwner);
        }
    }

    private void setIbanBic(SepaPaymentInfoModel paymentInfoModel, BankAccount bankAccount) {
        if (hasIban(bankAccount)) {
            paymentInfoModel.setIban(bankAccount.getIban());
            paymentInfoModel.setBic(bankAccount.getBic());
        }
    }

    private boolean hasIban(BankAccount bankAccount) {
        return bankAccount != null && !StringUtils.isEmpty(bankAccount.getIban());
    }

}


