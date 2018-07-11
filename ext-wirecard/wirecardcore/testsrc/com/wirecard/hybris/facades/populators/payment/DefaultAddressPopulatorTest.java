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

import com.wirecard.hybris.core.data.types.Address;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@UnitTest
public class DefaultAddressPopulatorTest {

    private AddressPopulator addressPopulator;

    @Mock
    private AddressModel source;
    @Mock
    private CountryModel country;

    private String city;
    private String line1;
    private String line2;
    private String postalCode;
    private String isocode;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        addressPopulator = new AddressPopulator();

        city = "CITY";
        line1 =  "STREET1";
        line2 = "STREET2";
        postalCode = "POSTAL_CODE";
        isocode = "COUNTRY";

        when(source.getTown()).thenReturn(city);
        when(source.getLine1()).thenReturn(line1);
        when(source.getLine2()).thenReturn(line2);
        when(source.getPostalcode()).thenReturn(postalCode);
        when(source.getCountry()).thenReturn(country);
        when(country.getIsocode()).thenReturn(isocode);

    }

    @Test
    public void populateTest() {

        Address target = new Address();
        addressPopulator.populate(source, target);

        //compare both datas
        assertEquals("City does not match", city, target.getCity());
        assertEquals("Street line 1 does not match", line1, target.getStreet1());
        assertEquals("Street line 2 does not match", line2, target.getStreet2());
        assertEquals("Postal code does not match", postalCode, target.getPostalCode());
        assertEquals("Country does not match", isocode, target.getCountry());

    }

}
