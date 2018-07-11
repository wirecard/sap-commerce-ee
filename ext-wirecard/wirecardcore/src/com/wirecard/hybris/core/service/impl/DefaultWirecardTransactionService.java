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

package com.wirecard.hybris.core.service.impl;

import com.wirecard.hybris.core.converter.xml.PaymentConverter;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.payment.transaction.WirecardTransactionData;
import com.wirecard.hybris.core.service.WirecardPaymentService;
import com.wirecard.hybris.core.service.WirecardTransactionService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class DefaultWirecardTransactionService implements WirecardTransactionService {

    private static final Logger LOGGER = Logger.getLogger(DefaultWirecardTransactionService.class);

    private static final String REQUEST = "-request";
    private static final String RESPONSE = "-response";
    private static final String TYPE = "text/xml";
    private static final String DOCUMENTS = "documents";
    private static final String FILE_EXT = ".xml";

    private ModelService modelService;
    private WirecardPaymentService wirecardPaymentService;
    private CommerceCheckoutService commerceCheckoutService;
    private PaymentConverter paymentConverter;
    private DefaultMediaService mediaService;

    /**
     * In this method we create the transaction for a specific cart and a specific payment type
     *
     * @param order
     *     the order
     * @param response
     *     the payment retrieved from wirecard
     * @param checkoutPaymentType
     *     the checkout payment type chosen by customer
     * @return payment transaction
     */
    private PaymentTransactionModel createTransaction(AbstractOrderModel order,
                                                      String checkoutPaymentType,
                                                      WirecardAuthenticationModel authenticationModel) {
        return getPaymentTransactionModel(order, checkoutPaymentType, authenticationModel);
    }

    @Override
    public PaymentTransactionModel createTransaction(WirecardTransactionData wirecardTransactionData) {
        return createTransaction(wirecardTransactionData.getAbstractOrderModel(), wirecardTransactionData.getCheckoutPaymentType(),
                                 wirecardTransactionData.getAuthenticationModel());
    }

    /**
     * This method return the transaction for a specific cart and a specific payment type
     *
     * @param order
     *     the order
     * @param checkoutPaymentType
     *     the checkout payment type chosen by customer
     * @return payment transaction
     */
    private PaymentTransactionModel getPaymentTransactionModel(AbstractOrderModel order,
                                                               String checkoutPaymentType,
                                                               WirecardAuthenticationModel authenticationModel) {
        PaymentTransactionModel transaction = lookForCompatibleTransactions(order, checkoutPaymentType);

        if (transaction == null) {
            transaction = getModelService().create(PaymentTransactionModel.class);
            transaction.setCode(getTransactionCode(order));
            transaction.setPaymentProvider(checkoutPaymentType);
            final List<PaymentTransactionModel> listTransactions = new ArrayList<>(
                order.getPaymentTransactions().size() + 1);
            listTransactions.addAll(order.getPaymentTransactions());
            listTransactions.add(transaction);
            order.setPaymentTransactions(listTransactions);
        }
        // authentication has always to be updated because of fallback scenario
        if (authenticationModel != null) {
            transaction.setAuthentication(authenticationModel);
        }
        getModelService().saveAll(order, transaction);
        return transaction;
    }

    /**
     * Method to get the Code of the transaction
     *
     * @param order
     *     The order
     * @return the code of the transaction
     */
    private String getTransactionCode(AbstractOrderModel order) {
        return order.getUser().getUid() + "_" + UUID.randomUUID();
    }

    /**
     * Method to resolve the the currency for transaction entry
     *
     * @param order
     *     The order
     * @return the currency for the order
     */
    private CurrencyModel getTransactionCurrency(AbstractOrderModel order) {
        return order.getCurrency();
    }

    /**
     * This method returns the transaction entry code
     *
     * @param transaction
     *     the transaction
     * @param paymentTransactionType
     *     the payment transaction type
     * @return the code
     */
    private String getNewPaymentTransactionEntryCode(PaymentTransactionModel transaction,
                                                     PaymentTransactionType paymentTransactionType) {
        int transactionNumber;
        if (CollectionUtils.isEmpty(transaction.getEntries())) {
            transactionNumber = 1;
        } else {
            transactionNumber = transaction.getEntries().size() + 1;
        }

        StringBuilder transactionEntryCode = new StringBuilder();
        transactionEntryCode.append(transaction.getCode()).append('-').append(paymentTransactionType.getCode())
                            .append('-').append(transactionNumber);
        return transactionEntryCode.toString();
    }


    /**
     * Creates a new PaymentTransactionEntry and saves it in parent PaymentTransaction
     *
     * @param wirecardTransactionData
     *     data that contains parameters necessary to create the entry
     * @param transaction
     *     the parent transaction
     * @return the payment transaction entry
     */
    private PaymentTransactionEntryModel createTransactionEntry(WirecardTransactionData wirecardTransactionData,
                                                                PaymentTransactionModel transaction) {
        PaymentTransactionEntryModel tranEntryToCreate =
            createAndFillTransactionEntry(wirecardTransactionData, transaction);
        calculateCart(wirecardTransactionData.getAbstractOrderModel());
        getModelService().saveAll(transaction, tranEntryToCreate);

        return tranEntryToCreate;
    }


    public void calculateCart(final AbstractOrderModel abstractOrderModel) {
        if (abstractOrderModel instanceof CartModel) {
            CommerceCheckoutParameter parameter = new CommerceCheckoutParameter();
            parameter.setEnableHooks(true);
            parameter.setCart((CartModel) abstractOrderModel);
            getCommerceCheckoutService().calculateCart(parameter);
        }
    }


    /**
     * Method to fill the fields of the PaymentTransactionEntry
     *
     * @param wirecardTransactionData
     *     Data required to fill the fields
     * @param transaction
     *     The parent transaction
     * @return the payment transaction entry
     */
    protected PaymentTransactionEntryModel createAndFillTransactionEntry(WirecardTransactionData wirecardTransactionData,
                                                                         PaymentTransactionModel transaction) {
        PaymentTransactionEntryModel entry = getModelService().create(PaymentTransactionEntryModel.class);

        entry.setType(wirecardTransactionData.getPaymentTransactionType());
        entry.setPaymentTransaction(transaction);
        entry.setCode(getNewPaymentTransactionEntryCode(transaction, wirecardTransactionData.getPaymentTransactionType()));
        entry.setTime(new Date());
        entry.setAmount(wirecardTransactionData.getTransactionAmount());
        // We create the transaction to be able to recover the amount that has been spent with the payment method
        if (wirecardTransactionData.getTransactionId() != null) {
            entry.setSubscriptionID(wirecardTransactionData.getTransactionId());
        }

        entry.setXmlFiles(getXmlFiles(wirecardTransactionData, entry.getCode()));

        Payment response = wirecardTransactionData.getResponse();
        fill(entry, response);

        AbstractOrderModel order = wirecardTransactionData.getAbstractOrderModel();
        entry.setCurrency(getTransactionCurrency(order));
        order.setStatus(getWirecardPaymentService().getOrderStatus(response));

        return entry;
    }

    /**
     * Method to fill the fields of the PaymentTransactionEntry from the Payment response
     *
     * @param entry
     *     The payment transaction entry
     * @param payment
     *     The payment response
     */
    private void fill(PaymentTransactionEntryModel entry, Payment payment) {
        if (payment != null) {
            if (payment.getProviderTransactionReferenceId() != null) {
                entry.setProviderTransactionReferenceId(payment.getProviderTransactionReferenceId());
            }
            String providerTransactionId = payment.getStatuses().getStatus().stream().findAny().map(Status::getProviderTransactionId)
                                                  .orElse(null);
            if (providerTransactionId != null) {
                entry.setProviderTransactionId(providerTransactionId);
            }
            entry.setRequestId(payment.getRequestId());
            entry.setTransactionStatus(getTransactionStatus(payment).name());
            entry.setTransactionStatusDetails(getTransactionStatusDetails(payment));
        }
    }


    @Override
    public PaymentTransactionEntryModel logTransactionData(WirecardTransactionData wirecardTransactionData) {

        PaymentTransactionModel transaction =
            createTransaction(wirecardTransactionData);
        return createTransactionEntry(wirecardTransactionData, transaction);
    }

    /**
     * Stores the xml of the request and response as media files
     *
     * @param wirecardTransactionData
     *     Contains the request and response
     * @param code
     *     The id for the file
     * @return the list of medias stored
     */
    private List<MediaModel> getXmlFiles(WirecardTransactionData wirecardTransactionData, String code) {
        List<MediaModel> medias = new ArrayList<>();

        // Stores the request xml
        if (wirecardTransactionData.getRequest() != null) {
            MediaModel requestMedia = storeXmlFile(wirecardTransactionData.getRequest(), code + REQUEST);
            medias.add(requestMedia);
        }
        // Stores the response xml
        if (wirecardTransactionData.getResponse() != null) {
            MediaModel responseMedia = storeXmlFile(wirecardTransactionData.getResponse(), code + RESPONSE);
            medias.add(responseMedia);
        }

        return medias;
    }

    @Override
    public PaymentTransactionModel lookForCompatibleTransactions(final AbstractOrderModel abstractOrder,
                                                                 final String checkoutPaymentType) {

        final List<PaymentTransactionModel> listTransactions = abstractOrder.getPaymentTransactions();

        /*
         * If we approve the payment, we will have to create an approved entry transaction
         */

        return listTransactions.stream()
                               .filter(transaction -> checkoutPaymentType.equalsIgnoreCase(transaction.getPaymentProvider()))
                               .findFirst()
                               .orElse(null);
    }

    @Override
    public boolean lookForAcceptedTransactions(AbstractOrderModel abstractOrderModel, PaymentTransactionType paymentTransactionType) {

        PaymentTransactionModel paymentTransactionModel = getPaymentTransaction(abstractOrderModel);

        PaymentTransactionEntryModel tranEntryAuthorizationAccepted =
            getNewestAcceptedTransactionEntry(paymentTransactionModel, paymentTransactionType);
        return tranEntryAuthorizationAccepted != null;
    }

    @Override
    public String getParentTransactionIdToOperate(AbstractOrderModel abstractOrderModel, PaymentTransactionType paymentTransactionType) {
        String result = null;
        PaymentTransactionModel paymentTransactionModel = getPaymentTransaction(abstractOrderModel);

        PaymentTransactionEntryModel acceptedTransactionEntry =
            getNewestAcceptedTransactionEntry(paymentTransactionModel, paymentTransactionType);
        if (acceptedTransactionEntry != null) {
            result = acceptedTransactionEntry.getSubscriptionID();
        }
        return result;
    }

    @Override
    public PaymentTransactionEntryModel getNewestAcceptedTransactionEntry(PaymentTransactionModel paymentTransactionModel,
                                                                          PaymentTransactionType paymentTransactionType) {

        Stream<PaymentTransactionEntryModel> acceptedEntries = paymentTransactionModel.getEntries().stream().filter(
            transactionEntry -> paymentTransactionType.equals(
                transactionEntry.getType())
                && TransactionStatus.ACCEPTED.name().equals(transactionEntry.getTransactionStatus()));

        return acceptedEntries.max(Comparator.comparing(PaymentTransactionEntryModel::getTime)).orElse(null);

    }

    @Override
    public PaymentTransactionModel getPaymentTransaction(AbstractOrderModel abstractOrder) {
        final List<PaymentTransactionModel> listTransactions = abstractOrder.getPaymentTransactions();
        String cartPaymentModeCode = abstractOrder.getPaymentMode().getCode();
        return listTransactions.stream()
                               .filter(transaction -> cartPaymentModeCode.equalsIgnoreCase(transaction.getPaymentProvider()))
                               .findFirst()
                               .orElse(null);
    }

    @Override
    public TransactionStatus getTransactionStatus(Payment response) {
        TransactionStatus transactionStatus = TransactionStatus.ERROR;
        if (response != null) {
            TransactionState transactionState = response.getTransactionState();
            if (transactionState == TransactionState.SUCCESS) {
                transactionStatus = TransactionStatus.ACCEPTED;
            } else {
                transactionStatus = TransactionStatus.REJECTED;
            }
        }
        return transactionStatus;
    }


    /**
     * We return the transaction status details for a wirecard response
     *
     * @param response
     *     the response from wirecard
     * @return transaction status details
     */
    private String getTransactionStatusDetails(Payment response) {
        return response.getStatuses().getStatus().stream().findFirst().map(Status::getDescription).orElseGet(null);
    }


    /**
     * Stores the xml of the payment as a media
     *
     * @param payment
     *     the payment to store
     * @param id
     *     the type of payment
     * @return the media file created
     */
    private MediaModel storeXmlFile(Payment payment, String id) {

        String xml;
        try {
            xml = getPaymentConverter().convertDataToXML(payment);
        } catch (WirecardPaymenException e) {
            LOGGER.error("Could not convert payment data to xml string", e);
            xml = "";
        }

        final MediaModel mediaModel = getModelService().create(CatalogUnawareMediaModel.class);
        mediaModel.setCode(id);
        mediaModel.setRealFileName(id + FILE_EXT);
        mediaModel.setMime(TYPE);
        mediaModel.setFolder(mediaService.getFolder(DOCUMENTS));
        getModelService().save(mediaModel);

        InputStream stream;
        try {
            stream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name()));
            mediaService.setStreamForMedia(mediaModel, stream);
            getModelService().refresh(mediaModel);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return mediaModel;
    }


    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    protected WirecardPaymentService getWirecardPaymentService() {
        return wirecardPaymentService;
    }

    @Required
    public void setWirecardPaymentService(WirecardPaymentService wirecardPaymentService) {
        this.wirecardPaymentService = wirecardPaymentService;
    }

    protected CommerceCheckoutService getCommerceCheckoutService() {
        return commerceCheckoutService;
    }


    @Required
    public void setCommerceCheckoutService(CommerceCheckoutService commerceCheckoutService) {
        this.commerceCheckoutService = commerceCheckoutService;
    }

    protected PaymentConverter getPaymentConverter() {
        return paymentConverter;
    }

    @Required
    public void setPaymentConverter(PaymentConverter paymentConverter) {
        this.paymentConverter = paymentConverter;
    }

    protected DefaultMediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(DefaultMediaService mediaService) {
        this.mediaService = mediaService;
    }


}
