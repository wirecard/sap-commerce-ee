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
import com.wirecard.hybris.core.data.types.PaymentMethods;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class PaymentMethodsPopulatorTest {

    @InjectMocks
    PaymentMethodsPopulator paymentMethodsPopulator;
    @Mock
    AbstractOrderModel source;
    @Mock
    PaymentModeModel paymentModeModel;
    @Mock
    ObjectFactory objectFactory;

    @Before
    public void setup() {

        String sourcePaymentMethodName = "masterpass";
        when(source.getPaymentMode()).thenReturn(paymentModeModel);
        when(paymentModeModel.getPaymentAlias()).thenReturn(sourcePaymentMethodName);
        when(objectFactory.createPaymentMethod()).thenReturn(new PaymentMethod());
        when(objectFactory.createPaymentMethods()).thenReturn(new PaymentMethods());

    }

    @Test
    public void populateTest() {

        Payment target = new Payment();

        paymentMethodsPopulator.populate(source, target);

        assertNotNull("Payment methods are null", target.getPaymentMethods());
        assertFalse("Payment method list is empty", target.getPaymentMethods().getPaymentMethod().isEmpty());

        paymentMethodsPopulator.setPaymentMethodName("CREDITCARD");

        paymentMethodsPopulator.populate(source, target);
        assertFalse("Payment method list is empty", target.getPaymentMethods().getPaymentMethod().isEmpty());

    }

}

