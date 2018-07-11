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

import com.wirecard.hybris.core.data.types.AccountHolder;
import com.wirecard.hybris.core.data.types.Consumer;
import com.wirecard.hybris.core.data.types.MerchantAccountId;
import com.wirecard.hybris.core.data.types.ObjectFactory;
import com.wirecard.hybris.core.data.types.OrderItems;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethod;
import com.wirecard.hybris.core.data.types.PaymentMethods;
import com.wirecard.hybris.core.data.types.Shipping;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.core.strategy.DescriptorGenerateStrategy;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * @author cprobst
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class OmniPaymentPopulatorTest {

    @Mock
    protected WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @InjectMocks
    protected OmniPaymentPopulator paymentPopulator;
    @Mock
    protected PaymentTransactionModel paymentTransactionModel;
    @Mock
    protected Converter<AddressModel, Consumer> consumerConverter;
    @Mock
    protected Converter<AddressModel, Shipping> shippingConverter;
    @Mock
    protected Converter<AddressModel, AccountHolder> accountHolderConverter;
    @Mock
    protected Converter<AbstractOrderModel, OrderItems> orderItemsConverter;

    @Mock
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    @Mock
    protected WirecardPaymentService wirecardPaymentService;
    @Mock
    protected WirecardTransactionService wirecardTransactionService;

    @Mock
    protected DescriptorGenerateStrategy descriptorGenerateStrategy;

    @Mock
    protected ObjectFactory objectFactory;

    @Mock
    protected OrderModel source;

    @Mock
    protected WirecardAuthenticationModel authentication;

    @Mock
    protected BaseSiteModel baseStoreModel;

    @Mock
    protected CurrencyModel currency;

    @Mock
    protected PaymentModeModel paymentMode;

    @Mock
    protected LanguageModel language;

    protected String maid = "MAID";

    @Before
    public void setup() {

        when(objectFactory.createMerchantAccountId()).thenReturn(new MerchantAccountId());
        when(objectFactory.createPaymentMethod()).thenReturn(new PaymentMethod());
        when(objectFactory.createPaymentMethods()).thenReturn(new PaymentMethods());

        when(wirecardPaymentConfigurationService.getAuthentication(source)).thenReturn(authentication);

        when(authentication.getMaid()).thenReturn(maid);

        when(paymentTransactionModel.getPaymentProvider()).thenReturn("test");

        when(wirecardTransactionService.getPaymentTransaction(source)).thenReturn(paymentTransactionModel);
        when(wirecardTransactionService.lookForCompatibleTransactions(Mockito.any(), Mockito.any())).thenReturn(paymentTransactionModel);

        when(source.getSite()).thenReturn(baseStoreModel);
        when(source.getGuid()).thenReturn("");
        when(source.getCurrency()).thenReturn(currency);
        when(source.getPaymentMode()).thenReturn(paymentMode);
        when(source.getLanguage()).thenReturn(language);

        when(currency.getIsocode()).thenReturn("EUR");

        when(paymentMode.getPaymentAlias()).thenReturn("creditcard");

    }

    @Test
    public void populateTest() {
        Payment target = new Payment();
        paymentPopulator.populate(source, target);
    }
}
