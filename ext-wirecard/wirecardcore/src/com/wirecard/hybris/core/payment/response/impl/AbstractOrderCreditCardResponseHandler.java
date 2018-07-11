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

import com.wirecard.hybris.core.data.types.Card;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOrderCreditCardResponseHandler extends DefaultOrderResponseHandler<CreditCardPaymentInfoModel> {

    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Override
    protected void fillPaymentInfoForPaymentMode(PaymentInfoModel wirecardPaymentInfoModel, PaymentOperationData data) {

        if (wirecardPaymentInfoModel instanceof CreditCardPaymentInfoModel) {

            CreditCardPaymentInfoModel creditCardPaymentInfoModel =
                (CreditCardPaymentInfoModel) wirecardPaymentInfoModel;

            Payment response = data.getPayment();

            creditCardPaymentInfoModel.setToken(response.getCardToken().getTokenId());
            creditCardPaymentInfoModel.setNumber(response.getCardToken().getMaskedAccountNumber());

            CreditCardType cardType = CreditCardType.CREDITCARD;
            String month = "";
            String year = "";

            if (response.getCard() != null) {
                Card card = response.getCard();
                if (card.getCardType() != null) {
                    cardType = CreditCardType.valueOf(card.getCardType().toString());
                }
                if (card.getExpirationMonth() != null) {
                    month = card.getExpirationMonth().toString();
                }
                if (card.getExpirationYear() != null) {
                    year = card.getExpirationYear().toString();
                }
            }

            creditCardPaymentInfoModel.setType(cardType);
            creditCardPaymentInfoModel.setValidToMonth(month);
            creditCardPaymentInfoModel.setValidToYear(year);
            // We cannot retreive this value from wirecard response
            creditCardPaymentInfoModel.setCcOwner("");

            boolean saveInAccount = data.isSaveInAccount();

            creditCardPaymentInfoModel.setSaved(saveInAccount);

            final CustomerModel customerModel = getCheckoutCustomerStrategy().getCurrentUserForCheckout();
            final List<PaymentInfoModel> paymentInfoModels = new ArrayList<>(customerModel.getPaymentInfos());
            if (!paymentInfoModels.contains(creditCardPaymentInfoModel)) {
                paymentInfoModels.add(creditCardPaymentInfoModel);
                if (saveInAccount) {
                    customerModel.setPaymentInfos(paymentInfoModels);
                    getModelService().save(customerModel);
                }

                getModelService().save(wirecardPaymentInfoModel);
                getModelService().refresh(customerModel);
            }
        }
    }


    protected CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
        return checkoutCustomerStrategy;
    }

    @Required
    public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }


}
