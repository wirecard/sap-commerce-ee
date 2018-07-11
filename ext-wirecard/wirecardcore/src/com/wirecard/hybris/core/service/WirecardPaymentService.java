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
import de.hybris.platform.basecommerce.enums.ReturnStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

public interface WirecardPaymentService {

    /**
     * This method will return the order status related to Payment response
     *
     * @param response
     *     the response from wirecard
     * @return return status
     */
    ReturnStatus getReturnStatus(Payment response);

    /**
     * Update the return status for all return entries in {@link ReturnRequestModel}
     *
     * @param returnRequest
     *     - the return request
     * @param status
     *     - the return status
     */
    void setReturnRequestStatus(final ReturnRequestModel returnRequest, final ReturnStatus status);

    /**
     * This method will return the order status related to Payment response
     *
     * @param response
     *     the response from wirecard
     * @return order status
     */
    OrderStatus getOrderStatus(Payment response);

    /**
     * Store the pares in the payment transaction
     *
     * @param abstractOrderModel
     *     the order
     */
    void storePares(AbstractOrderModel abstractOrderModel, String pares);

    /**
     * Stores the token in the payment transaction
     *
     * @param abstractOrderModel
     *      the order
     * @param token
     *      the token
     */
    void storeToken(AbstractOrderModel abstractOrderModel, String token);

    /**
     * Stores 3D parameters in the payment transaction
     *
     * @param abstractOrderModel
     *      the order
     * @param cardholderAuthenticationStatus
     *      the cardholder Authentication Status
     * @param cardholderAuthenticationValue
     *      the cardholder Authentication Value
     * @param eci
     *      the Eci
     * @param xid
     *      the Xid
     */
    void storeMPIthreeDparameters(AbstractOrderModel abstractOrderModel,
                                  String cardholderAuthenticationStatus,
                                  String cardholderAuthenticationValue,
                                  String eci,
                                  String xid);
}
