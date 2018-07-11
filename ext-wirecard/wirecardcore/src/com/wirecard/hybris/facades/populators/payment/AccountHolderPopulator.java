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
import com.wirecard.hybris.core.data.types.Address;
import com.wirecard.hybris.core.data.types.Gender;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import java.text.SimpleDateFormat;
import java.util.Map;

public class AccountHolderPopulator implements Populator<AddressModel, AccountHolder> {

    private Converter<AddressModel, Address> addressConverter;
    private Map<de.hybris.platform.core.enums.Gender, Gender> genderMapping;

    @Override
    public void populate(AddressModel source, AccountHolder target) throws ConversionException {
        target.setFirstName(source.getFirstname());
        target.setLastName(source.getLastname());
        target.setPhone(source.getPhone1());
        populateEmail(source, target);
        target.setAddress(getAddressConverter().convert(source));

        if (source.getDateOfBirth() != null) {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            target.setDateOfBirth(format.format(source.getDateOfBirth()));
        }

        if (source.getGender() != null) {
            target.setGender(genderMapping.get(source.getGender()));
        }
    }

    private void populateEmail(AddressModel source, AccountHolder target) {
        String email = source.getEmail();
        if (email == null) {
            if (source.getOwner() instanceof AbstractOrderModel) {
                email = ((CustomerModel) ((AbstractOrderModel) source.getOwner()).getUser()).getContactEmail();
            } else if (source.getOwner() instanceof CustomerModel) {
                email = ((CustomerModel) source.getOwner()).getContactEmail();
            }
        }

        target.setEmail(email);
    }

    protected Converter<AddressModel, Address> getAddressConverter() {
        return addressConverter;
    }

    @Required
    public void setAddressConverter(Converter<AddressModel, Address> addressConverter) {
        this.addressConverter = addressConverter;
    }

    protected Map<de.hybris.platform.core.enums.Gender, Gender> getGenderMapping() {
        return genderMapping;
    }

    @Required
    public void setGenderMapping(Map<de.hybris.platform.core.enums.Gender, Gender> genderMapping) {
        this.genderMapping = genderMapping;
    }
}
