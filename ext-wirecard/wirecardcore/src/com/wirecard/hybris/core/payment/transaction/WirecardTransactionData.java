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
package com.wirecard.hybris.core.payment.transaction;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;

import java.math.BigDecimal;

public class WirecardTransactionData {

    private AbstractOrderModel abstractOrderModel;

    private String checkoutPaymentType;

    private Payment response;

    private Payment request;

    private PaymentTransactionType paymentTransactionType;

    private String transactionId;

    private BigDecimal transactionAmount;


    private WirecardAuthenticationModel authenticationModel;


    public void setAbstractOrderModel(final AbstractOrderModel abstractOrderModel) {
        this.abstractOrderModel = abstractOrderModel;
    }


    public AbstractOrderModel getAbstractOrderModel() {
        return abstractOrderModel;
    }


    public void setCheckoutPaymentType(final String checkoutPaymentType) {
        this.checkoutPaymentType = checkoutPaymentType;
    }


    public String getCheckoutPaymentType() {
        return checkoutPaymentType;
    }


    public void setResponse(final Payment response) {
        this.response = response;
    }


    public Payment getResponse() {
        return response;
    }


    public void setRequest(final Payment request) {
        this.request = request;
    }


    public Payment getRequest() {
        return request;
    }


    public void setPaymentTransactionType(final PaymentTransactionType paymentTransactionType) {
        this.paymentTransactionType = paymentTransactionType;
    }


    public PaymentTransactionType getPaymentTransactionType() {
        return paymentTransactionType;
    }


    public void setTransactionId(final String transactionId) {
        this.transactionId = transactionId;
    }


    public String getTransactionId() {
        return transactionId;
    }


    public void setTransactionAmount(final BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }


    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public WirecardAuthenticationModel getAuthenticationModel() {
        return authenticationModel;
    }

    public void setAuthenticationModel(WirecardAuthenticationModel authenticationModel) {
        this.authenticationModel = authenticationModel;
    }

}
