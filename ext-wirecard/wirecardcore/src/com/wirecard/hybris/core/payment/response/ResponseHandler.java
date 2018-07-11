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

package com.wirecard.hybris.core.payment.response;

import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.math.BigDecimal;

public interface ResponseHandler<T extends ItemModel> {

    /**
     * This method creates a transaction using the abstractOrderModel of the cart, creates a transactionEntry of the
     * transtaction type and uses it to create PaymentInfoModel of the payment method and saves it.
     *
     * @param item
     *            The model that stores the data of the affected item.
     * @param data
     *            The payment data object which contains payment based on Wirecard's xml schema.
     * @throws WirecardPaymenException
     *             thrown when an error during response processing occurs
     */
    void processResponse( T item, final PaymentOperationData data) throws WirecardPaymenException;

    /**
     * Method to get the order given the itemModel
     *
     * @param item
     *     The item which is used to determine the order
     * @return the order
     */
    AbstractOrderModel getOrder(T item);

    /**
     * Method to resolve the amount for transaction entry
     *
     * @param item
     *     The item which is used to determine the amount
     * @return the amount for the transaction
     */
    BigDecimal getTransactionAmount(T item);
}
