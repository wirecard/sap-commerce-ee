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

package com.wirecard.hybris.addon.forms;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@UnitTest
public class WirecardPaymentDetailsFormTest {

    private WirecardPaymentDetailsForm wirecardPaymentDetailsForm;

    private String paymentId;
    private String firstName;
    private String amount;
    private String billToCountry;
    private String comments;
    private String currency;
    private String key;
    private String value;
    private Boolean newBillingAddress;
    @Mock
    private AddressForm billingAddress;
    @Mock
    private Map<String, String> parameters;

    @Before
    public void setup() {

        MockitoAnnotations.initMocks(this);

        paymentId = "PAYMENTID";
        newBillingAddress = false;
        firstName = "FIRSTNAME";
        amount = "AMOUNT";
        billToCountry = "BILLTOCOUNTRY";
        comments = "COMMENTS";
        currency = "CURRENCY";
        key = "KEY";
        value = "VALUE";

        wirecardPaymentDetailsForm = new WirecardPaymentDetailsForm();
        wirecardPaymentDetailsForm.setPaymentId(paymentId);
        wirecardPaymentDetailsForm.setNewBillingAddress(newBillingAddress);

        billingAddress = new AddressForm();
        billingAddress.setFirstName(firstName);
        wirecardPaymentDetailsForm.setBillingAddress(billingAddress);

        wirecardPaymentDetailsForm.setAmount(amount);
        wirecardPaymentDetailsForm.setBillToCountry(billToCountry);
        wirecardPaymentDetailsForm.setComments(comments);
        wirecardPaymentDetailsForm.setCurrency(currency);

        parameters = new HashMap<>();
        parameters.put(key, value);
        wirecardPaymentDetailsForm.setParameters(parameters);
    }

    @Test
    public void paymentIdTest(){
        assertEquals("payment ID does not match",paymentId,wirecardPaymentDetailsForm.getPaymentId());
    }
    @Test
    public void newBillingAddressTest(){
        assertEquals("New Billing Address does not match",newBillingAddress,wirecardPaymentDetailsForm.isNewBillingAddress());
    }
    @Test
    public void billingAddressTest(){
        assertEquals("Billing Address does not match",billingAddress,wirecardPaymentDetailsForm.getBillingAddress());
    }
    @Test
    public void amountTest(){
        assertEquals("Amount does not match",amount,wirecardPaymentDetailsForm.getAmount());
    }
    @Test
    public void billToCountryTest(){
        assertEquals("Bill to Country does not match",billToCountry,wirecardPaymentDetailsForm.getBillToCountry());
    }
    @Test
    public void commentsTest(){
        assertEquals("Comments does not match",comments,wirecardPaymentDetailsForm.getComments());
    }
    @Test
    public void currencyTest(){
        assertEquals("Currency does not match",currency,wirecardPaymentDetailsForm.getCurrency());
    }
    @Test
    public void parametersTest(){
        assertEquals("Currency does not match",parameters,wirecardPaymentDetailsForm.getParameters());
    }

}
