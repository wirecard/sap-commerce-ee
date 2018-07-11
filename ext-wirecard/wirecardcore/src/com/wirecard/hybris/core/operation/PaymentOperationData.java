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

package com.wirecard.hybris.core.operation;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;

/**
 * This calls is used to transport data to the payment operation. It is not guaranteed that all fields having an value
 */
public class PaymentOperationData {

    private String pares;

    private String tokenId;

    private Payment payment;

    private boolean saveInAccount;

    private boolean isSavedCC;

    private WirecardAuthenticationModel wirecardAuthenticationModel;

    private WirecardAuthenticationModel wirecardFallbackAuthenticationModel;

    private String sepaAccountOwner;

    private String bic;

    private String iban;

    public String getPares() {
        return pares;
    }

    public void setPares(String pares) {
        this.pares = pares;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public boolean isSaveInAccount() {
        return saveInAccount;
    }

    public void setSaveInAccount(boolean saveInAccount) {
        this.saveInAccount = saveInAccount;
    }

    public WirecardAuthenticationModel getWirecardAuthenticationModel() {
        return wirecardAuthenticationModel;
    }

    public void setWirecardAuthenticationModel(WirecardAuthenticationModel wirecardAuthenticationModel) {
        this.wirecardAuthenticationModel = wirecardAuthenticationModel;
    }

    public WirecardAuthenticationModel getWirecardFallbackAuthenticationModel() {
        return wirecardFallbackAuthenticationModel;
    }

    public void setWirecardFallbackAuthenticationModel(WirecardAuthenticationModel wirecardFallbackAuthenticationModel) {
        this.wirecardFallbackAuthenticationModel = wirecardFallbackAuthenticationModel;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getSepaAccountOwner() {
        return sepaAccountOwner;
    }

    public void setSepaAccountOwner(String sepaAccountOwner) {
        this.sepaAccountOwner = sepaAccountOwner;
    }

    public boolean isSavedCC() {
        return isSavedCC;
    }

    public void setSavedCC(boolean savedCC) {
        isSavedCC = savedCC;
    }
}
