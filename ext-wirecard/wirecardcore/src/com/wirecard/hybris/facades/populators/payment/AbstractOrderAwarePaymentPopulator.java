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

import com.wirecard.hybris.core.data.types.Payment;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * @author cprobst
 */
public abstract class AbstractOrderAwarePaymentPopulator implements Populator<ItemModel, Payment> {

    /**
     * {@inheritDoc}
     */
    @Override
    public final void populate(ItemModel source, Payment target) throws ConversionException {
        AbstractOrderModel order;
        if (source instanceof AbstractOrderModel) {
            order = (AbstractOrderModel) source;
        } else if (source instanceof ReturnRequestModel) {
            ReturnRequestModel returnRequest = (ReturnRequestModel) source;
            order = returnRequest.getOrder();
        } else {
            throw new IllegalArgumentException("Unsuppported item passed " + source.getClass().getName());
        }

        doPopulate(order, target);
    }

    /**
     * Method called by {@link #populate(ItemModel, Payment)} after the AbstractOrderModel was resolved
     *
     * @param order
     *     The resolved abstract order model
     * @param target
     *     The payment object top populate
     */
    public abstract void doPopulate(AbstractOrderModel order, Payment target) throws ConversionException;

}
