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

package com.wirecard.hybris.core.operation.impl;

import com.wirecard.hybris.core.data.types.ObjectFactory;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.ThreeD;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.operation.PaymentProcessor;
import com.wirecard.hybris.exception.WirecardPaymenException;

public class CreditCardAuthorizationParesPaymentProcessor implements PaymentProcessor {

    private ObjectFactory objectFactory;

    @Override
    public void processPayment(Payment payment, PaymentOperationData data) throws WirecardPaymenException {
        String pares = data.getPares();
        ThreeD threeD = objectFactory.createThreeD();
        threeD.getContent().add(objectFactory.createThreeDPares(pares));

        payment.setThreeD(threeD);
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }
}
