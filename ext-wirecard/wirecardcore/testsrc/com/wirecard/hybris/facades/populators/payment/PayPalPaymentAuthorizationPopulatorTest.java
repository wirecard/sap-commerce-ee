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


import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PayPalPaymentAuthorizationPopulatorTest extends OmniPaymentPopulatorTest {

    private static final String MAID = "maid";
    private static final String SUCCESS_URL = "successURL";

    @InjectMocks
    private OmniPaymentPopulator paymentPopulator;

    private TransactionTypePopulator transactionTypePopulator;


    @Override
    @Before
    public void setup() {
        super.setup();
        when(wirecardPaymentConfigurationService.getSuccesURL(Mockito.anyObject())).thenReturn(SUCCESS_URL);

        WirecardAuthenticationModel authentication = new WirecardAuthenticationModel();
        authentication.setMaid(MAID);
        WirecardPaymentConfigurationModel configurationForPaymentMode = new WirecardPaymentConfigurationModel();
        configurationForPaymentMode.setAuthentication(authentication);
        when(wirecardPaymentConfigurationService.getConfiguration(Mockito.anyObject())).thenReturn(configurationForPaymentMode);

        transactionTypePopulator = new TransactionTypePopulator();
        transactionTypePopulator.setTransactionType(TransactionType.AUTHORIZATION);

    }

    @Test
    public void testPopulate() throws Exception {

        Payment target = new Payment();

        paymentPopulator.populate(source, target);
        transactionTypePopulator.populate(source, target);

        assertEquals("Transaction type isn't Authorization", TransactionType.AUTHORIZATION, target.getTransactionType());
        assertEquals("Success URL is not correct", SUCCESS_URL, target.getSuccessRedirectUrl());

    }
}
