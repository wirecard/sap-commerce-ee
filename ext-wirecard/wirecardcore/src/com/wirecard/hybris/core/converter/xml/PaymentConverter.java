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

package com.wirecard.hybris.core.converter.xml;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.exception.WirecardInvalidSignatureException;
import com.wirecard.hybris.exception.WirecardPaymenException;


public interface PaymentConverter {


    /**
     * This method convert a object of Payment to xml
     *
     * @param paymentData
     *            The object with the data that we are going to send to wirecard
     * @return the xml string for the given PaymentData
     * @throws WirecardPaymenException
     */
    String convertDataToXML(Payment paymentData) throws WirecardPaymenException;


    /**
     * This method convert a xml to a Payment object and signature validation is disabled
     *
     * @param xml
     *            The xml with the data from to wirecard
     * @return the Payment object that represents the xml
     * @throws WirecardPaymentException
     *             when an error during conversion occurs
     */
    Payment convertXMLToData(String xml) throws WirecardPaymenException;

    /**
     * @param xml
     *     The xml with the data from to wirecard
     * @param validateSignature
     *     If true a valid signature has to be available be aware that no signature also causes an error
     * @return the Payment object that represents the xml
     * @throws WirecardInvalidSignatureException
     *     Only thrown if validateSignature is set to true, when the signature is not present or invalid
     * @throws WirecardPaymentException
     *     when an error during conversion occurs
     */
    Payment convertXMLToData(String xml, boolean validateSignature) throws WirecardPaymenException;

}
