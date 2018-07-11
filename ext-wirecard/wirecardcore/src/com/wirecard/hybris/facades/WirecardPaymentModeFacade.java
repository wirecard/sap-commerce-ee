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

import com.wirecard.hybris.core.converter.data.PaymentModeData;
import com.wirecard.hybris.core.data.SepaMandateData;
import com.wirecard.hybris.core.data.WirecardRequestData;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.TransactionType;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;

import java.util.List;

public interface WirecardPaymentModeFacade {

    /**
     * This method search for all the active payment methods
     *
     * @return a list with the active payment methods
     */
    List<PaymentModeData> getActivePaymentModes();

    /**
     * This method search on the active payment methods for an specific credit card method
     *
     * @param paymentModeData
     *     the list of actives payment methods
     * @param alias
     *     the specific credit card payment method that wants to be searched
     * @return a boolean indicating if that credit card method is active
     */
    PaymentModeData getActiveCreditCardPaymentMode(List<PaymentModeData> paymentModeData, String alias);

    /**
     * This method search on the active payment methods for WD_UNIONPAY
     *
     * @param paymentModeData
     *     the list of actives payment methods
     * @return a boolean indicating if unionpay is active
     */
    boolean isUnionpayPaymentModeActive(List<PaymentModeData> paymentModeData);

    /**
     * This method returns a boolean that indicates if the method payment is inactive
     *
     * @param paymentMethodChosen
     *     the payment method that wants to know if is inactive
     * @return a boolean that indicates if the payment method is inactive
     */
    boolean isPaymentMethodChooseInactive(String paymentMethodChosen);

    /**
     * This method returns a boolean that indicates if the transaction type of a specific method is PURCHASE or PURCHASE_WITH_HOP
     *
     * @param paymentMethodChosen
     *     the payment method that wants to know if meets the requirements
     * @return a boolean that indicates if the transaction type is PURCHASE or PURCHASE_WITH_HOP
     */
    boolean isPurchase(String paymentMethodChosen);

    /**
     * This method creates the parameters requested to render and send a seamless form
     *
     * @param abstractOrderModel
     *     the order
     * @param transactionType
     *     type of transaction
     * @param paymentMethodName
     *     payment method
     * @return the parameters requested to render and send a seamless form
     */
    WirecardRequestData getSeamlessFormData(AbstractOrderModel abstractOrderModel,
                                            TransactionType transactionType,
                                            PaymentMethodName paymentMethodName, PaymentModeModel paymentModeModel);

    /**
     * This method returns the sepa dynamic data for sepa mandate text pop up
     *
     * @param sepaCode
     *     The sepa payment mode code
     * @return The sepa dynamic mandate data for sepa mandate text pop up
     */
    SepaMandateData getSepaMandateData(String sepaCode);


    /**
     * This method returns if a payment method is active
     *
     * @param paymentMethodChosen
     *     the paymentMethod
     */
    boolean isPaymentMethodActive(String paymentMethodChosen);
}
