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
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.warehousing.returns.service.RefundAmountCalculationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPaymentRefundPopulatorTest extends OmniPaymentPopulatorTest {

    @InjectMocks
    private DefaultPaymentRefundPopulator defaultPaymentRefundPopulator;


    @Mock
    private ReturnRequestModel returnRequestModel;

    @Mock
    private RefundAmountCalculationService refundAmountCalculationService;


    @Mock
    private List<PaymentTransactionModel> paymentTransactionsList;

    @Mock
    private LanguageModel language;

    private BigDecimal customRefundAmount;
    private BigDecimal amountToRefund;

    @Override
    @Before
    public void setup() {

        super.setup();
        customRefundAmount = BigDecimal.ZERO;
        amountToRefund = BigDecimal.TEN;

        when(returnRequestModel.getOrder()).thenReturn(source);

        when(source.getPaymentTransactions())
            .thenReturn(paymentTransactionsList);

        when(refundAmountCalculationService.getCustomRefundAmount(returnRequestModel)).thenReturn(customRefundAmount);
        when(refundAmountCalculationService.getOriginalRefundAmount(returnRequestModel)).thenReturn(amountToRefund);

        when(source.getCurrency())
            .thenReturn(currency);

        when(currency.getIsocode())
            .thenReturn("EUR");

        when(source.getGuid())
            .thenReturn("Guid");

        when(returnRequestModel.getOrder().getLanguage()).thenReturn(language);
        when(source.getTotalPrice()).thenReturn(100.d);

        when(language.getIsocode()).thenReturn("EN");

    }


    @Override
    @Test
    public void populateTest() {
        Payment target = new Payment();

        paymentPopulator.populate(returnRequestModel, target);
        defaultPaymentRefundPopulator.populate(returnRequestModel, target);

        assertEquals("Entry mode is not ECOMMERCE", EntryMode.ECOMMERCE, target.getEntryMode());
        assertEquals("Locale is not EN", "EN", target.getLocale());
        assertNotNull("Requested amount is null", target.getRequestedAmount());
        assertEquals("Requested amount value does not match", amountToRefund, target.getRequestedAmount().getValue());
        assertEquals("Requested amount currency does not match", "EUR", target.getRequestedAmount().getCurrency());

    }

}
