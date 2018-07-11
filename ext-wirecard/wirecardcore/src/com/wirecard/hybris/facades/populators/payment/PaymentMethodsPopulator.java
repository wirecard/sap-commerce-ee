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

package com.wirecard.hybris.facades.populators.payment;

import com.wirecard.hybris.core.data.types.ObjectFactory;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethod;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.PaymentMethods;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.regex.Pattern;

public class PaymentMethodsPopulator extends AbstractOrderAwarePaymentPopulator {

    private static final Pattern TRIM_SCRIPT = Pattern.compile("[-]");

    private ObjectFactory wirecardObjectFactory;
    private String paymentMethodName;

    @Override
    public void doPopulate(AbstractOrderModel source, Payment target) {

        PaymentMethodName paymentMethodNameObj = null;

        if (getPaymentMethodName() != null) {
            paymentMethodNameObj = PaymentMethodName.valueOf(getPaymentMethodName());
        } else {
            paymentMethodNameObj = getPaymentMethodName(source);

        }

        PaymentMethods paymentMethods = getWirecardObjectFactory().createPaymentMethods();
        PaymentMethod paymentMethod = getWirecardObjectFactory().createPaymentMethod();
        paymentMethods.getPaymentMethod().add(paymentMethod);
        target.setPaymentMethods(paymentMethods);
        target.getPaymentMethods().getPaymentMethod().get(0).setName(paymentMethodNameObj);

    }

    protected ObjectFactory getWirecardObjectFactory() {
        return wirecardObjectFactory;
    }

    @Required
    public void setWirecardObjectFactory(ObjectFactory wirecardObjectFactory) {
        this.wirecardObjectFactory = wirecardObjectFactory;
    }

    public PaymentMethodName getPaymentMethodName(AbstractOrderModel source) {
        String name = TRIM_SCRIPT.matcher(StringUtils.upperCase(source.getPaymentMode().getPaymentAlias())).replaceAll("_");
        return PaymentMethodName.valueOf(name);
    }

    protected String getPaymentMethodName() {
        return paymentMethodName;
    }

    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }
}
