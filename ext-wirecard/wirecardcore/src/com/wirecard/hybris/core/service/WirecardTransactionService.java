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

package com.wirecard.hybris.core.service;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.payment.transaction.WirecardTransactionData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

public interface WirecardTransactionService {

    /**
     * This method creates the PaymentTransaction and PaymentTransactionEntry
     *
     * @return PaymentTransactionEntryModel
     */
    PaymentTransactionEntryModel logTransactionData(WirecardTransactionData wirecardTransactionData);

    /**
     * @param wirecardTransactionData
     *     which contains data which should be written to transaction
     * @return the create transaction
     */
    PaymentTransactionModel createTransaction(WirecardTransactionData wirecardTransactionData);

    /**
     * We will look for existing compatible transactions to modify the codes with the new one made
     *
     * @param abstractOrder
     *     the abstractOrder to look for in transactions
     * @param checkoutPaymentType
     *     the transaction payment type
     * @return an existing payment transaction for the given abstractOrder and payment type
     */
    PaymentTransactionModel lookForCompatibleTransactions(AbstractOrderModel abstractOrder,
                                                          String checkoutPaymentType);

    /**
     * We will look for specific transactions to check if abstract order is authorized
     *
     * @param abstractOrderModel
     *     the abstractOrder to look for in transactions
     * @param paymentTransactionType
     *     transaction type to look
     * @return an existing payment transaction for the given abstractOrder and payment type
     */

    boolean lookForAcceptedTransactions(AbstractOrderModel abstractOrderModel,
                                        PaymentTransactionType paymentTransactionType);

    /**
     * Searches if there exists a parent transaction and retreives it
     *
     * @param abstractOrderModel
     *  the order
     * @param paymentTransactionType
     *  the type of transaction
     * @return the subscription id of the parent transaction
     */
    String getParentTransactionIdToOperate(AbstractOrderModel abstractOrderModel,
                                           PaymentTransactionType paymentTransactionType);

    /**
     * Searches for the last Accepted Transaction entry for the given transactionType
     *
     * @param paymentTransactionModel
     *  The transaction
     * @param paymentTransactionType
     *  The type of transaction
     * @return  the found transaction entry
     */
    PaymentTransactionEntryModel getNewestAcceptedTransactionEntry(PaymentTransactionModel paymentTransactionModel,
                                                                   PaymentTransactionType paymentTransactionType);

    /**
     * This method return the payment transaction of the choose payment mode
     *
     * @param abstractOrder
     *     the cart
     * @return the payment transaction
     */
    PaymentTransactionModel getPaymentTransaction(AbstractOrderModel abstractOrder);

    /**
     * This method will return the transaction status related to Payment response
     *
     * @param response
     *     the response from wirecard
     * @return transaction status
     */
    TransactionStatus getTransactionStatus(Payment response);

    /**
     * Calculates the abstract order only if it is a cart
     *
     * @param abstractOrderModel
     *     the cart to be calculated
     */
    void calculateCart(final AbstractOrderModel abstractOrderModel);
}
