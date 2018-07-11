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

import com.wirecard.hybris.core.converter.data.WirecardPaymentInfoData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PoipiaPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.WirecardDebitPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

public class WirecardPaymentInfoPopulator implements Populator<PaymentInfoModel, WirecardPaymentInfoData> {
    private Converter<AddressModel, AddressData> addressConverter;

    protected Converter<AddressModel, AddressData> getAddressConverter() {
        return addressConverter;
    }

    @Required
    public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter) {
        this.addressConverter = addressConverter;
    }

    @Override
    public void populate(PaymentInfoModel source, WirecardPaymentInfoData target)
        throws ConversionException {

        if (source.getBillingAddress() != null) {
            target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
        }

        if (source instanceof WirecardDebitPaymentInfoModel) {
            WirecardDebitPaymentInfoModel paymentInfoModel = (WirecardDebitPaymentInfoModel)source;
            target.setIban(paymentInfoModel.getIban());
            target.setBic(paymentInfoModel.getBic());
        }

        if (source instanceof PoipiaPaymentInfoModel) {
            PoipiaPaymentInfoModel paymentInfoModel = (PoipiaPaymentInfoModel)source;
            target.setBankName(paymentInfoModel.getBankName());
            target.setBranchAddress(paymentInfoModel.getBranchAddress());
            target.setBranchCity(paymentInfoModel.getBranchCity());
            target.setBranchState(paymentInfoModel.getBranchState());
            target.setProviderTransactionReferenceId(paymentInfoModel.getProviderTransactionReferenceId());
        }
    }
}
