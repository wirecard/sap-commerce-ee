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
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@UnitTest
public class DefaultAccountHolderPopulatorTest {

    private AccountHolderPopulator accountHolderPopulator;
    private AddressModel source;
    private Map<de.hybris.platform.core.enums.Gender, Gender> genderMapping;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Date date;
    private String formattedDate;

    @Mock
    private Address address;
    @Mock
    private Converter<AddressModel, Address> addressConverter;
    @Mock
    private AddressModel sourceGender;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        firstName = "FIRSTNAME";
        lastName = "LASTNAME";
        phone = "123456789";
        email = "e@mail.com";

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DATE, 12);
        date = calendar.getTime();

        formattedDate = "12-01-2010";

        source = new AddressModel();
        source.setEmail(email);
        source.setFirstname(firstName);
        source.setLastname(lastName);
        source.setPhone1(phone);
        source.setDateOfBirth(date);

        genderMapping = new HashMap<>();
        genderMapping.put(de.hybris.platform.core.enums.Gender.MALE, Gender.M);
        genderMapping.put(de.hybris.platform.core.enums.Gender.FEMALE, Gender.F);

        accountHolderPopulator = new AccountHolderPopulator();
        accountHolderPopulator.setAddressConverter(addressConverter);
        accountHolderPopulator.setGenderMapping(genderMapping);

        when(addressConverter.convert(source)).thenReturn(address);

    }

    @Test
    public void populateTest() {

        AccountHolder target = new AccountHolder();
        accountHolderPopulator.populate(source, target);

        //compare both datas
        assertEquals("First name does not match", firstName, target.getFirstName());
        assertEquals("Last name does not match", lastName, target.getLastName());
        assertEquals("Phone does not match", phone, target.getPhone());
        assertEquals("Email does not match", email, target.getEmail());
        assertEquals("Address does not match", address, target.getAddress());
        assertEquals("Date of birth does not match", formattedDate, target.getDateOfBirth());
    }

    @Test
    public void populateGenderMaleTest() {
        when(sourceGender.getGender()).thenReturn(de.hybris.platform.core.enums.Gender.MALE);

        AccountHolder target = new AccountHolder();
        accountHolderPopulator.populate(sourceGender, target);

        assertEquals("Gender does not match", Gender.M, target.getGender());
    }


    @Test
    public void populateGenderFemaleTest() {
        when(sourceGender.getGender()).thenReturn(de.hybris.platform.core.enums.Gender.FEMALE);

        AccountHolder target = new AccountHolder();
        accountHolderPopulator.populate(sourceGender, target);

        assertEquals("Gender does not match", Gender.F, target.getGender());
    }
}
