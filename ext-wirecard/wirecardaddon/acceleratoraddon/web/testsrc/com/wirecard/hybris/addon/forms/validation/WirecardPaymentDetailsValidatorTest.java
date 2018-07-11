/*********************************************************************
 * The Initial Developer of the content of this file is NETCONOMY.
 * All portions of the code written by NETCONOMY are property of
 * NETCONOMY. All Rights Reserved.
 *
 * NETCONOMY Software & Consulting GmbH
 * Hilmgasse 4, A-8010 Graz (Austria)
 * FN 204360 f, Landesgericht fuer ZRS Graz
 * Tel: +43 (316) 815 544
 * Fax: +43 (316) 815544-99
 * www.netconomy.net
 *
 * (c) 2018 by NETCONOMY Software & Consulting GmbH
 *********************************************************************/

package com.wirecard.hybris.addon.forms.validation;

import com.wirecard.hybris.addon.forms.WirecardPaymentDetailsForm;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cmsfacades.data.SyncJobRequestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@UnitTest
public class WirecardPaymentDetailsValidatorTest {

    private WirecardPaymentDetailsValidator wirecardPaymentDetailsValidator;

    private WirecardPaymentDetailsForm object;
    private Errors errors;
    private AddressForm billingAddress;

    private String paymentID;
    private String firstName;
    private String lastName;
    private String line1;
    private String townCity;
    private String postCode;
    private String countryIso;
    private SyncJobRequestData syncJobRequestData;

    private static final String SOURCE = "source";
    private static final String CATALOG = "catalog";

    @Before
    public void setup() {

        wirecardPaymentDetailsValidator = new WirecardPaymentDetailsValidator();

        object = new WirecardPaymentDetailsForm();
        paymentID = "PAYMENTID";
        firstName = "FIRSTNAME";
        lastName = "LASTNAME";
        line1 = "LINE1";
        townCity = "TOWNCITY";
        postCode = "POSTCODE";
        countryIso = "COUNTRYISO";

        object.setPaymentId(paymentID);
        billingAddress = new AddressForm();
        billingAddress.setFirstName(firstName);
        billingAddress.setLastName(lastName);
        billingAddress.setLine1(line1);
        billingAddress.setTownCity(townCity);
        billingAddress.setPostcode(postCode);
        billingAddress.setCountryIso(countryIso);
        object.setBillingAddress(billingAddress);

        syncJobRequestData = new SyncJobRequestData();
        syncJobRequestData.setCatalogId(CATALOG);
        syncJobRequestData.setSourceVersionId(SOURCE);


    }

    @Test
    public void validateSuccessTest() {
        errors = createErrors();
        wirecardPaymentDetailsValidator.validate(object, errors);

        Assert.assertTrue("Class is not supported.", wirecardPaymentDetailsValidator.supports(object.getClass()));
        Assert.assertFalse("There should be no validation errors.", errors.hasErrors());
    }

    @Test
    public void validateErrorTest() {
        errors = createErrors();

        object.getBillingAddress().setFirstName(null);
        wirecardPaymentDetailsValidator.validate(object, errors);

        Assert.assertTrue("Class is not supported.", wirecardPaymentDetailsValidator.supports(object.getClass()));
        Assert.assertTrue("There should be validation errors.", errors.hasErrors());
        Assert.assertEquals("First Name is not entered.", "billingAddress.firstName", errors.getFieldErrors().get(0).getField());

    }

    private Errors createErrors() {
        return new BeanPropertyBindingResult(object, object.getClass().getSimpleName());
    }


}
