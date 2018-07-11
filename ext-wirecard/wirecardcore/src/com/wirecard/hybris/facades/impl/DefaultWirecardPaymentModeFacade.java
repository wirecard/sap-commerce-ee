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

package com.wirecard.hybris.facades.impl;

import com.wirecard.hybris.core.converter.data.PaymentModeData;
import com.wirecard.hybris.core.data.SepaMandateData;
import com.wirecard.hybris.core.data.WirecardRequestData;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.core.enums.WirecardTransactionType;
import com.wirecard.hybris.core.model.WirecardPaymentConfigurationModel;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardPaymentModeService;
import com.wirecard.hybris.core.strategy.WirecardSignatureStrategy;
import com.wirecard.hybris.facades.WirecardPaymentModeFacade;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.springframework.beans.factory.annotation.Required;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

public class DefaultWirecardPaymentModeFacade implements WirecardPaymentModeFacade {

    private static final String REQUEST_DATA_DATE_FORMAT = "yyyyMMddHHmmss";
    private static final String UTC = "UTC";
    private static final String WD_UNIONPAY = "wd-unionpayinternational";

    private I18NService i18NService;
    private WirecardPaymentModeService wirecardPaymentModeService;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    private WirecardSignatureStrategy wirecardSignatureStrategy;

    private Converter<PaymentModeModel, PaymentModeData> paymentModeConverter;

    @Override
    public List<PaymentModeData> getActivePaymentModes() {

        List<PaymentModeModel> activeMethods =
            wirecardPaymentConfigurationService.getAllAllowedPaymentModes();

        return getPaymentModeConverter().convertAll(activeMethods);
    }

    public PaymentModeData getActiveCreditCardPaymentMode(List<PaymentModeData> paymentModeData, String alias) {

        return paymentModeData.stream().filter(paymentMode -> paymentMode.getPaymentAlias().equals(alias)).findFirst().orElse(null);

    }

    public boolean isUnionpayPaymentModeActive(List<PaymentModeData> paymentModeData) {

        return paymentModeData.stream().anyMatch(paymentMode -> paymentMode.getCode().equals(WD_UNIONPAY));

    }

    @Override
    public boolean isPurchase(String paymentMethodChosen) {

        PaymentModeModel paymentMode = wirecardPaymentModeService.getPaymentModeByCode(paymentMethodChosen);

        return WirecardTransactionType.PURCHASE.equals(paymentMode.getTransactionType())
            || WirecardTransactionType.PURCHASE_WITH_HOP.equals(paymentMode.getTransactionType());
    }

    @Override
    public WirecardRequestData getSeamlessFormData(AbstractOrderModel abstractOrderModel,
                                                   TransactionType transactionType,
                                                   PaymentMethodName paymentMethodName, PaymentModeModel paymentModeModel) {

        WirecardRequestData wirecardRequestData = new WirecardRequestData();
        Date newDate = new Date();

        SimpleDateFormat format = new SimpleDateFormat(REQUEST_DATA_DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone(UTC));
        String dateToStr = format.format(newDate);

        wirecardRequestData.setRequestTimeStamp(dateToStr);
        wirecardRequestData.setRequestId(abstractOrderModel.getGuid()
                                                           .concat(paymentMethodName.value())
                                                           .concat(String.valueOf(newDate.getTime())));
        wirecardRequestData.setMerchantAccountId(wirecardSignatureStrategy.getMaid(paymentModeModel));
        wirecardRequestData.setTransactionType(transactionType.value());
        wirecardRequestData.setRequestedAmount(String.valueOf(abstractOrderModel.getTotalPrice()));
        wirecardRequestData.setRequestedAmountCurrency(abstractOrderModel.getCurrency().getIsocode());
        wirecardRequestData.setLocale(getI18NService().getCurrentLocale().getLanguage());
        wirecardRequestData.setPaymentMethod(paymentMethodName.value());
        wirecardRequestData.setRequestSignature(wirecardSignatureStrategy.generateSignatureV1(wirecardRequestData, paymentModeModel));

        return wirecardRequestData;
    }

    @Override
    public SepaMandateData getSepaMandateData(String sepaCode) {
        SepaMandateData sepaMandateData = null;
        Optional<PaymentModeModel> sepaPaymentMode = wirecardPaymentConfigurationService.getAllAllowedPaymentModes()
                                                                                        .stream()
                                                                                        .filter(p -> p.getCode().equals(sepaCode))
                                                                                        .findAny();
        if (sepaPaymentMode.isPresent()) {
            WirecardPaymentConfigurationModel configuration = wirecardPaymentConfigurationService.getConfiguration(sepaPaymentMode.get());
            if (configuration != null) {
                sepaMandateData = new SepaMandateData();
                sepaMandateData.setCreditorId(configuration.getAuthentication().getCreditorId());
                sepaMandateData.setCreditorName(configuration.getAuthentication().getCreditorName());
                sepaMandateData.setStoreCity(configuration.getAuthentication().getStoreCity());
            }
        }

        return sepaMandateData;
    }

    @Override
    public boolean isPaymentMethodActive(String paymentMethodChosen) {
        return getActivePaymentModes().stream()
                                      .map(PaymentModeData::getCode)
                                      .anyMatch(paymentModeCode -> paymentModeCode.equals(paymentMethodChosen));
    }

    @Override
    public boolean isPaymentMethodChooseInactive(String paymentMethodChosen) {
        return getActivePaymentModes().stream()
                                      .map(PaymentModeData::getCode)
                                      .noneMatch(paymentModeCode -> paymentModeCode.equals(paymentMethodChosen));
    }

    protected I18NService getI18NService() {
        return i18NService;
    }

    @Required
    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }

    protected Converter<PaymentModeModel, PaymentModeData> getPaymentModeConverter() {
        return paymentModeConverter;
    }

    @Required
    public void setPaymentModeConverter(Converter<PaymentModeModel, PaymentModeData> paymentModeConverter) {
        this.paymentModeConverter = paymentModeConverter;
    }

    protected WirecardPaymentModeService getWirecardPaymentModeService() {
        return wirecardPaymentModeService;
    }

    @Required
    public void setWirecardPaymentModeService(WirecardPaymentModeService wirecardPaymentModeService) {
        this.wirecardPaymentModeService = wirecardPaymentModeService;
    }

    protected WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }

    protected WirecardSignatureStrategy getWirecardSignatureStrategy() {
        return wirecardSignatureStrategy;
    }

    @Required
    public void setWirecardSignatureStrategy(WirecardSignatureStrategy wirecardSignatureStrategy) {
        this.wirecardSignatureStrategy = wirecardSignatureStrategy;
    }

}
