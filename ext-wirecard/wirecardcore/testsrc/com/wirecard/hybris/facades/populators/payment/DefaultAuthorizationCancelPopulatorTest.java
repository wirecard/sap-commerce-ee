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

import com.wirecard.hybris.core.data.types.EntryMode;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultAuthorizationCancelPopulatorTest extends OmniPaymentPopulatorTest {


    private TransactionTypePopulator transactionTypePopulator;

    private ParentTransactionIdPopulator parentTransactionIdPopulator;


    private static final String IPADDRESS = "::1";

    private static final String APTID = "APTID";

    private static final String REQUESTID = "111-";

    private static final String LOCALE = "en";

    @Override
    @Before
    public void setup() {
        super.setup();
        WirecardAuthenticationModel authentication = new WirecardAuthenticationModel();
        WirecardPaymentConfigurationModel configurationForPaymentMode = new WirecardPaymentConfigurationModel();
        configurationForPaymentMode.setAuthentication(authentication);
        when(wirecardPaymentConfigurationService.getConfiguration(Mockito.anyObject())).thenReturn(configurationForPaymentMode);

        when(wirecardTransactionService.getParentTransactionIdToOperate(Mockito.any(), Mockito.any())).thenReturn(APTID);

        when(language.getIsocode()).thenReturn(LOCALE);
        when(source.getGuid()).thenReturn(REQUESTID);

        PaymentModeModel paymentMode = Mockito.mock(PaymentModeModel.class);
        when(paymentMode.getPaymentAlias()).thenReturn("paypal");

        when(source.getPaymentMode()).thenReturn(paymentMode);
        transactionTypePopulator = new TransactionTypePopulator();
        transactionTypePopulator.setTransactionType(TransactionType.VOID_AUTHORIZATION);

        parentTransactionIdPopulator = new ParentTransactionIdPopulator();
        parentTransactionIdPopulator.setParentPaymentTransactionType(PaymentTransactionType.AUTHORIZATION);
        parentTransactionIdPopulator.setWirecardTransactionService(wirecardTransactionService);
    }

    @Override
    @Test
    public void populateTest() {
        Payment target = new Payment();

        paymentPopulator.populate(source, target);
        transactionTypePopulator.populate(source, target);
        parentTransactionIdPopulator.populate(source, target);

        assertTrue("Request ID does not start with " + REQUESTID, target.getRequestId().startsWith(REQUESTID));
        assertEquals("Transaction type does not match", TransactionType.VOID_AUTHORIZATION, target.getTransactionType());
        assertNotNull("Merchant account id is empty", target.getMerchantAccountId());
        assertEquals("EntryMode does not match", EntryMode.ECOMMERCE, target.getEntryMode());
        assertEquals("IPADDRESS does not match", IPADDRESS, target.getIpAddress());
        assertEquals("ParentTransactionId does not match", APTID, target.getParentTransactionId());
        assertEquals("Locale does not match", LOCALE, target.getLocale());
    }

}
