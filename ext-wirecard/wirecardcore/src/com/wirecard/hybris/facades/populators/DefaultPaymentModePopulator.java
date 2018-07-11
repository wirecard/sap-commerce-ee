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

package com.wirecard.hybris.facades.populators;

import com.wirecard.hybris.core.converter.data.PaymentModeData;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

public class DefaultPaymentModePopulator implements Populator<PaymentModeModel, PaymentModeData> {

    private Converter<MediaModel, MediaData> mediaConverter;

    private Converter<CountryModel, CountryData> countryConverter;

    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;

    @Override
    public void populate(PaymentModeModel source, PaymentModeData target) throws ConversionException {
        target.setCode(source.getCode());
        target.setPaymentAlias(source.getPaymentAlias());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setMedia(getMediaConverter().convert(source.getMedia()));
        target.setSupportedBillingCountries(getCountryConverter().convertAll(wirecardPaymentConfigurationService.getSupportedCountries(
            source)));
        target.setShowOptionalFormField(source.getShowOptionalFormField());
        target.setSameAddress(wirecardPaymentConfigurationService.getSameAddress(source));
    }

    protected Converter<MediaModel, MediaData> getMediaConverter() {
        return mediaConverter;
    }

    @Required
    public void setMediaConverter(Converter<MediaModel, MediaData> mediaConverter) {
        this.mediaConverter = mediaConverter;
    }

    protected Converter<CountryModel, CountryData> getCountryConverter() {
        return countryConverter;
    }

    @Required
    public void setCountryConverter(Converter<CountryModel, CountryData> countryConverter) {
        this.countryConverter = countryConverter;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }
}
