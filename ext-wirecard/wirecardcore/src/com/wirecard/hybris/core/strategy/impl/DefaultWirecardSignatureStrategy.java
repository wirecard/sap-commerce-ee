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

package com.wirecard.hybris.core.strategy.impl;

import com.wirecard.hybris.core.data.WirecardRequestData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.strategy.WirecardSignatureStrategy;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

public class DefaultWirecardSignatureStrategy implements WirecardSignatureStrategy {

    private static final String REQUESTED_AMOUNT = "requested_amount";
    private static final Logger LOGGER = Logger.getLogger(DefaultWirecardSignatureStrategy.class);

    private static final String REQUEST_TIME_STAMP = "request_time_stamp";
    private static final String REQUEST_ID = "request_id";
    private static final String MERCHANT_ACCOUNT_ID = "merchant_account_id";
    private static final String TRANSACTION_TYPE = "transaction_type";
    private static final String REQUESTED_AMOUNT_CURRENCY = "requested_amount_currency";
    private static final String SECRET_KEY = "secret_key";

    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    public String generateSignatureV1(WirecardRequestData requestDataToEncrypt, PaymentModeModel paymentModeModel) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(REQUEST_TIME_STAMP, requestDataToEncrypt.getRequestTimeStamp());
        map.put(REQUEST_ID, requestDataToEncrypt.getRequestId());
        map.put(MERCHANT_ACCOUNT_ID, requestDataToEncrypt.getMerchantAccountId());
        map.put(TRANSACTION_TYPE, requestDataToEncrypt.getTransactionType());
        map.put(REQUESTED_AMOUNT, requestDataToEncrypt.getRequestedAmount());
        map.put(REQUESTED_AMOUNT_CURRENCY, requestDataToEncrypt.getRequestedAmountCurrency());
        map.put(SECRET_KEY, getSecret(paymentModeModel));

        return tosha256(map);
    }

    private String getSecret(PaymentModeModel paymentModeModel) {

        return getWirecardPaymentConfigurationService().getConfiguration(paymentModeModel)
                                                       .getAuthentication()
                                                       .getSecret();
    }

    public String getMaid(PaymentModeModel paymentModeModel) {

        return getWirecardPaymentConfigurationService().getConfiguration(paymentModeModel)
                                                       .getAuthentication()
                                                       .getMaid();
    }

    /**
     * Wirecard method to generate a valid signature
     *
     * @param fields
     *     map with the necessary fields
     * @return a string with the signature
     */
    private static String tosha256(Map<String, String> fields) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> field : fields.entrySet()) {
                sb.append(field.getValue().trim());
            }
            md.update(sb.toString().getBytes(StandardCharsets.UTF_8));
            byte[] mdbytes = md.digest();
            return DatatypeConverter.printHexBinary(mdbytes);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.warn("Error creating signature", e);
        }
        return null;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }
}
