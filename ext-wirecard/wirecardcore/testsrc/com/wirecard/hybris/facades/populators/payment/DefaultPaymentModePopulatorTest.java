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

import com.wirecard.hybris.core.converter.data.PaymentModeData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.facades.populators.DefaultPaymentModePopulator;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPaymentModePopulatorTest {

    @InjectMocks
    private DefaultPaymentModePopulator defaultPaymentModePopulator;

    @Mock
    private PaymentModeModel paymentModeModel;
    @Mock
    private PaymentModeData paymentModeData;
    @Mock
    private Converter<MediaModel, MediaData> mediaConverter;
    @Mock
    private Converter<CountryModel, CountryData> countryConverter;
    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Before
    public void setup() {

        when(paymentModeModel.getCode())
            .thenReturn(StringUtils.EMPTY);
        when(paymentModeModel.getName())
            .thenReturn(StringUtils.EMPTY);
        when(paymentModeData.getCode())
            .thenReturn(StringUtils.EMPTY);
        when(paymentModeData.getName())
            .thenReturn(StringUtils.EMPTY);
    }

    @Test
    public void populateTest() {

        defaultPaymentModePopulator.populate(paymentModeModel, paymentModeData);

        //compare both datas
        assertTrue("The payment mode is not correct", paymentModeModel.getCode().equals(paymentModeData.getCode()));
        assertTrue("The payment model is not correct", paymentModeModel.getName().equals(paymentModeData.getName()));

    }

}