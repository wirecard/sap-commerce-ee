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

import com.wirecard.hybris.core.data.types.Card;
import com.wirecard.hybris.core.data.types.CardToken;
import com.wirecard.hybris.core.data.types.EntryMode;
import com.wirecard.hybris.core.data.types.MerchantAccountId;
import com.wirecard.hybris.core.data.types.Money;
import com.wirecard.hybris.core.data.types.Notification;
import com.wirecard.hybris.core.data.types.Notifications;
import com.wirecard.hybris.core.data.types.ObjectFactory;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.Periodic;
import com.wirecard.hybris.core.data.types.PeriodicType;
import com.wirecard.hybris.core.data.types.SequenceType;
import com.wirecard.hybris.core.data.types.ThreeD;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.core.strategy.DescriptorGenerateStrategy;
import com.wirecard.hybris.facades.WirecardHopPaymentOperationsFacade;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.math.BigDecimal;

public class OmniPaymentPopulator extends AbstractOrderAwarePaymentPopulator {

    private static final String IPADDRESS = "::1";
    private static final String CREDITCARD = "creditcard";


    private ObjectFactory wirecardObjectFactory;
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    private WirecardTransactionService wirecardTransactionService;
    private WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade;

    private DescriptorGenerateStrategy descriptorGenerateStrategy;

    @Override
    public void doPopulate(AbstractOrderModel source, Payment target) throws ConversionException {
        target.setRequestId(source.getGuid().concat(String.valueOf(System.currentTimeMillis())));
        target.setOrderNumber(source.getGuid());

        target.setDescriptor(getDescriptor(source));
        target.setEntryMode(EntryMode.ECOMMERCE);
        target.setMerchantAccountId(getMerchantId(source));

        populateCardToken(source, target);
        populateRecurringCreditCard(source, target);
        populateThreeDInfo(source, target);

        target.setIpAddress(IPADDRESS);

        if (source instanceof OrderModel) {
            target.setLocale(((OrderModel) source).getLanguage().getIsocode());
        } else {
            target.setLocale(source.getSite().getLocale());
        }

        target.setSuccessRedirectUrl(getWirecardPaymentConfigurationService().getSuccesURL(source.getPaymentMode()));
        target.setCancelRedirectUrl(getWirecardPaymentConfigurationService().getCancelURL(source.getPaymentMode()));

        populateNotifications(source, target);

        populateRequestedAmount(source, target);

    }

    /**
     * @return the descriptorGenerateStrategy
     */
    public DescriptorGenerateStrategy getDescriptorGenerateStrategy() {
        return descriptorGenerateStrategy;
    }

    /**
     * @param descriptorGenerateStrategy
     *     the descriptorGenerateStrategy to set
     */
    public void setDescriptorGenerateStrategy(DescriptorGenerateStrategy descriptorGenerateStrategy) {
        this.descriptorGenerateStrategy = descriptorGenerateStrategy;
    }

    private String getDescriptor(AbstractOrderModel abstractOrderModel) {
        return getDescriptorGenerateStrategy().getDescriptor(abstractOrderModel);
    }

    private void populateRequestedAmount(AbstractOrderModel source, Payment target) {
        Money money = new Money();
        money.setCurrency(source.getCurrency().getIsocode());
        money.setValue(BigDecimal.valueOf(source.getTotalPrice()));
        target.setRequestedAmount(money);
    }

    private void populateNotifications(AbstractOrderModel source, Payment target) {
        Notifications notifications = new Notifications();
        Notification notification = new Notification();
        notification.setUrl(getWirecardPaymentConfigurationService().getNotificationsURL(source));
        notifications.getNotification().add(notification);
        target.setNotifications(notifications);
    }

    private void populateThreeDInfo(AbstractOrderModel abstractOrderModel, Payment payment) {
        PaymentTransactionModel paymentTransactionModel = getWirecardTransactionService()
            .getPaymentTransaction(abstractOrderModel);
        if (paymentTransactionModel.getPares() != null && !paymentTransactionModel.getPares().isEmpty()) {
            payment.setThreeD(getThreeD(abstractOrderModel));
        } else if (paymentTransactionModel.getCardholderAuthenticationStatus() != null
            && !paymentTransactionModel.getCardholderAuthenticationStatus().isEmpty()) {
            payment.setThreeD(getThreeD(paymentTransactionModel));
        }
    }

    private MerchantAccountId getMerchantId(AbstractOrderModel source) {
        MerchantAccountId merchantAccountId = getWirecardObjectFactory().createMerchantAccountId();
        String maid = getWirecardPaymentConfigurationService().getAuthentication(source).getMaid();
        merchantAccountId.setValue(maid);
        return merchantAccountId;
    }

    private void populateCardToken(AbstractOrderModel abstractOrderModel, Payment target) {
        PaymentTransactionModel paymentTransactionModel = getWirecardTransactionService()
            .getPaymentTransaction(abstractOrderModel);
        String tokenId = paymentTransactionModel.getTokenId();
        if (tokenId != null) {
            CardToken cardToken = new CardToken();
            cardToken.setTokenId(tokenId);
            target.setCardToken(cardToken);
        }
    }

    private void populateRecurringCreditCard(AbstractOrderModel abstractOrderModel, Payment target) {

        if (abstractOrderModel.getPaymentMode().getPaymentAlias().equals(CREDITCARD)) {
            if (wirecardHopPaymentOperationsFacade.isSaveInAccount()) {
                setPeriodic(target, SequenceType.FIRST);
                setCardTokenizationFlag(target);

            } else if (wirecardHopPaymentOperationsFacade.isSavedCC()) {
                setPeriodic(target, SequenceType.RECURRING);
                setCardTokenizationFlag(target);
            }
        }
    }

    private void setPeriodic(Payment target, SequenceType sequenceType) {
        Periodic periodic = wirecardObjectFactory.createPeriodic();
        periodic.setPeriodicType(PeriodicType.CI);
        periodic.setSequenceType(sequenceType);
        target.setPeriodic(periodic);
    }

    private void setCardTokenizationFlag(Payment target) {
        Card card = wirecardObjectFactory.createCard();
        card.setMerchantTokenizationFlag(true);
        target.setCard(card);
    }

    private ThreeD getThreeD(PaymentTransactionModel paymentTransactionModel) {
        String cardholderAuthenticationStatus = paymentTransactionModel.getCardholderAuthenticationStatus();
        String cardholderAuthenticationValue = paymentTransactionModel.getCardholderAuthenticationValue();
        String eci = paymentTransactionModel.getEci();
        String xid = paymentTransactionModel.getXid();

        ThreeD threeD = wirecardObjectFactory.createThreeD();
        threeD.getContent().add(wirecardObjectFactory.createThreeDCardholderAuthenticationStatus(cardholderAuthenticationStatus));
        threeD.getContent().add(wirecardObjectFactory.createThreeDCardholderAuthenticationValue(cardholderAuthenticationValue));
        threeD.getContent().add(wirecardObjectFactory.createThreeDEci(eci));
        threeD.getContent().add(wirecardObjectFactory.createThreeDXid(xid));

        return threeD;
    }

    private ThreeD getThreeD(AbstractOrderModel abstractOrderModel) {

        ThreeD threeD = getWirecardObjectFactory().createThreeD();
        PaymentTransactionModel paymentTransactionModel = getWirecardTransactionService()
            .getPaymentTransaction(abstractOrderModel);
        threeD.getContent().add(getWirecardObjectFactory().createThreeDPares(paymentTransactionModel.getPares()));
        return threeD;

    }

    protected ObjectFactory getWirecardObjectFactory() {
        return wirecardObjectFactory;
    }

    @Required
    public void setWirecardObjectFactory(ObjectFactory wirecardObjectFactory) {
        this.wirecardObjectFactory = wirecardObjectFactory;
    }

    public PaymentMethodName getPaymentMethodName(AbstractOrderModel source) {
        return PaymentMethodName.valueOf(StringUtils.upperCase(source.getPaymentMode().getPaymentAlias()));
    }

    public WirecardPaymentConfigurationService getWirecardPaymentConfigurationService() {
        return wirecardPaymentConfigurationService;
    }

    @Required
    public void setWirecardPaymentConfigurationService(WirecardPaymentConfigurationService wirecardPaymentConfigurationService) {
        this.wirecardPaymentConfigurationService = wirecardPaymentConfigurationService;
    }

    protected WirecardTransactionService getWirecardTransactionService() {
        return wirecardTransactionService;
    }

    @Required
    public void setWirecardTransactionService(WirecardTransactionService wirecardTransactionService) {
        this.wirecardTransactionService = wirecardTransactionService;
    }

    protected WirecardHopPaymentOperationsFacade getWirecardHopPaymentOperationsFacade() {
        return wirecardHopPaymentOperationsFacade;
    }

    @Required
    public void setWirecardHopPaymentOperationsFacade(WirecardHopPaymentOperationsFacade wirecardHopPaymentOperationsFacade) {
        this.wirecardHopPaymentOperationsFacade = wirecardHopPaymentOperationsFacade;
    }
}
