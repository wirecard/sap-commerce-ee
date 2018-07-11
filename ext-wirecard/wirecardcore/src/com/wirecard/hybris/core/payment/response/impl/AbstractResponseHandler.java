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

package com.wirecard.hybris.core.payment.response.impl;

import com.wirecard.hybris.core.data.types.Severity;
import com.wirecard.hybris.core.data.types.Status;
import com.wirecard.hybris.core.data.types.TransactionState;
import com.wirecard.hybris.core.operation.PaymentOperationData;
import com.wirecard.hybris.core.payment.response.ResponseHandler;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractResponseHandler<T extends ItemModel> implements ResponseHandler<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractResponseHandler.class);
    private static final String DATA_NULL = "The data, payment or statuses should not be null";

    private ModelService modelService;
    private CommerceCheckoutService commerceCheckoutService;
    private FlexibleSearchService flexibleSearchService;

    @Override
    public final void processResponse(T item, final PaymentOperationData data) throws WirecardPaymenException {

        if (shouldProcessResponse(item, data)) {
            try {
                TransactionState transactionState = data.getPayment().getTransactionState();

                if (TransactionState.FAILED.equals(transactionState)) {
                    getOrder(item).setPaymentStatus(PaymentStatus.ERROR);
                    getModelService().save(getOrder(item));
                    List<Status> statusList = getStatusList(data);
                    String errorMessage = statusList.stream()
                                                    .filter(status -> status.getSeverity() == Severity.ERROR
                                                        || status.getSeverity() == Severity.WARNING)
                                                    .map(Status::getDescription)
                                                    .collect(Collectors.joining("<br/>"));
                    throw new WirecardPaymenException(errorMessage);
                }

                doProcessResponse(item, data);
            } catch (WirecardPaymenException e) {
                onException(item, data, e);
            }
        }
    }

    protected boolean shouldProcessResponse(T item, PaymentOperationData data) {
        // all responses are processed by default
        return true;
    }

    private List<Status> getStatusList(PaymentOperationData data) {
        List<Status> statusList;
        try {
            ServicesUtil.validateParameterNotNull(data, "The data should not be null");
            ServicesUtil.validateParameterNotNull(data.getPayment(), "The payment should not be null");
            ServicesUtil.validateParameterNotNull(data.getPayment().getStatuses(), "The statuses should not be null");
            statusList = data.getPayment().getStatuses().getStatus();
            ServicesUtil.validateParameterNotNull(statusList, "The statusList should not be null");
        } catch (IllegalArgumentException e) {
            LOG.error(DATA_NULL, e);
            statusList = Collections.emptyList();
        }
        return statusList;
    }

    protected void doProcessResponse(T item, PaymentOperationData data) throws WirecardPaymenException {
        // do nothing by default
    }

    protected void onException(T item, PaymentOperationData data, WirecardPaymenException exception) throws WirecardPaymenException {
        // the input exception is thrown by default
        throw exception;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    @Required
    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    protected CommerceCheckoutService getCommerceCheckoutService() {
        return commerceCheckoutService;
    }

    @Required
    public void setCommerceCheckoutService(CommerceCheckoutService commerceCheckoutService) {
        this.commerceCheckoutService = commerceCheckoutService;
    }
}
