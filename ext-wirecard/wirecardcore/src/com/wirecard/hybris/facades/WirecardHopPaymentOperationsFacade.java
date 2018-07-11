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

package com.wirecard.hybris.facades;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;

public interface WirecardHopPaymentOperationsFacade {

    /**
     * This method retrieves a payment mode choosen by the customer
     * executes the proper payment operation and return a response
     *
     * @param data
     *     additionalParameters
     * @return a paymentResponse
     * @throws WirecardPaymenException
     *     throws an exception if we have
     *     problems with the response
     */
    Payment executePaymentOperation(PaymentOperationData data) throws
        WirecardPaymenException;

    /**
     * This method retrieves a payment mode choosen by the customer
     * and executes specific payment operation and return a response
     *
     * @param operation
     *     paymentOperation
     * @param data
     *     additionalParameters
     * @return a paymentResponse
     * @throws WirecardPaymenException
     *     throws an exception if we have
     *     problems with the response
     */
    Payment executePaymentOperation(String operation, PaymentOperationData data) throws
        WirecardPaymenException;

    /**
     * This method dicover the payment mode by the orderCode related
     * and executes specific payment operation and return a response
     *
     * @param operation
     *     paymentOperation
     * @param data
     *     additionalParameters
     * @param orderCode
     *     the order code related
     * @return a paymentResponse
     * @throws WirecardPaymenException
     *     throws an exception if we have
     *     problems with the response
     */
    Payment executePaymentOperation(String operation, PaymentOperationData data, String orderCode) throws
        WirecardPaymenException;

    /**
     * This method returns the response obtained from wirecard
     *
     * @param paymentCode
     *     the code of the payment method is being processed
     * @param requestId
     *     the code of the request is being processed
     * @return the response getted from wirecard
     */
    String getPaymentResponseFromWirecard(String paymentCode, String requestId);

    /**
     * obtains from the session the bic of ideal
     *
     * @return the big of ideal stored on the session
     */
    String getIdealBic();

    /**
     * saves on the session the bic of ideal
     *
     * @param bic
     *     indicates the bic for ideal
     */
    void setIdealBic(String bic);

    /**
     * obtains from the session if the credit card is already saved on the account
     *
     * @return a boolean that indicates if the credit card is saved on the account
     */
    boolean isSaveInAccount();

    /**
     * saves on the session if the credit card is saved in the account
     *
     * @param isSaveInAccount
     *     indicates if is already saved the credit card on the account
     */
    void setIsSaveInAccount(boolean isSaveInAccount);

    /**
     * obtains from the session if the credit card is already saved
     *
     * @return a boolean that indicates if the credit card is saved
     */
    boolean isSavedCC();

    /**
     * saves on the session if the credit card is saved
     *
     * @param isSavedCC
     *     indicates if is already saved the credit card
     */
    void setIsSavedCC(boolean isSavedCC);

    /**
     * Convert and decode messages from wirecard uses base64 to decode
     *
     * @param content
     *     a received message
     * @return payment data
     * @throws WirecardPaymenException
     *     if an invalid signature or no signature was provided or parsing was not possible
     */
    Payment parseMessage(String content, boolean isEncoded, boolean checkSignature) throws WirecardPaymenException;
}
