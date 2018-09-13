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

package com.wirecard.hybris.core.paymentmethods.converters;

import com.wirecard.hybris.core.converter.xml.PaymentConverter;
import com.wirecard.hybris.core.converter.xml.impl.DefaultWirecardPaymentConverter;
import com.wirecard.hybris.core.data.types.MerchantAccountId;
import com.wirecard.hybris.core.data.types.Money;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.data.types.TransactionType;
import com.wirecard.hybris.exception.WirecardInvalidSignatureException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;


@UnitTest
public class PayPalAuthorizationPaymentRequestConverterTest extends HybrisJUnit4TransactionalTest {

    /**
     * Edit the local|project.properties to change logging behaviour (properties log4j.*).
     */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PayPalAuthorizationPaymentRequestConverterTest.class);

    private PaymentConverter xmlConverter;

    @Before
    public void setUp() {

        xmlConverter = new DefaultWirecardPaymentConverter();
    }


    /**
     * Check the correct working of the xml converter. First converting de data to xml and making conversion to data
     * again with the reverse converter. Afterwards, it compares if initial data and the reconverted data are the same
     * comparing all the fields.
     *
     * @throws WirecardPaymenException
     * @throws WirecardInvalidSignatureException
     */
    @Test
    public void testPayPalRequestConverter() throws WirecardInvalidSignatureException, WirecardPaymenException {

        Payment payment = generateTestPayPalRequestData();

        String xmlString = xmlConverter.convertDataToXML(payment);
        log.debug(xmlString);
        log.info(xmlString);

        Payment paymentInverse = xmlConverter.convertXMLToData(xmlString);

        assertTrue("The original data is not equal to the reverse converted data, for XML to Pojo",
                   paypalConvertedDataComparer(payment, paymentInverse));

    }


    private Payment generateTestPayPalRequestData() {

        Payment payment = new Payment();

        MerchantAccountId merchantAccountId = new MerchantAccountId();
        merchantAccountId.setValue("9abf05c1-c266-46ae-8eac-7f87ca97af28");
        payment.setMerchantAccountId(merchantAccountId);
        payment.setRequestId("15485962698001");
        payment.setTransactionType(TransactionType.AUTHORIZATION);
        Money money = new Money();
        money.setCurrency("EUR");
        money.setValue(BigDecimal.valueOf(480928));
        payment.setRequestedAmount(money);
        com.wirecard.hybris.core.data.types.AccountHolder accountHolder = new com.wirecard.hybris.core.data.types.AccountHolder();
        accountHolder.setFirstName("John");
        accountHolder.setLastName("Constantine");
        payment.setAccountHolder(accountHolder);
        com.wirecard.hybris.core.data.types.Shipping shipping = new com.wirecard.hybris.core.data.types.Shipping();
        shipping.setFirstName("Jack");
        shipping.setLastName("Jones");
        shipping.setPhone("+49123123123");
        com.wirecard.hybris.core.data.types.Address address = new com.wirecard.hybris.core.data.types.Address();
        address.setStreet1("123 anystree");
        address.setCity("Chicago");
        address.setCountry("US");
        address.setState("IL");
        address.setPostalCode("10101");
        shipping.setAddress(address);
        payment.setShipping(shipping);
        payment.setOrderNumber("48090");
        payment.setDescriptor("customerStatement 18009998888");
        com.wirecard.hybris.core.data.types.PaymentMethods paymentMethods = new com.wirecard.hybris.core.data.types.PaymentMethods();
        com.wirecard.hybris.core.data.types.PaymentMethod paymentMethod = new com.wirecard.hybris.core.data.types.PaymentMethod();
        paymentMethod.setName(PaymentMethodName.PAYPAL);
        paymentMethods.getPaymentMethod().add(paymentMethod);
        payment.setPaymentMethods(paymentMethods);
        payment.setSuccessRedirectUrl("https://www.google.es");
        payment.setCancelRedirectUrl("https://www.google.es");

        return payment;


    }


    private boolean paypalConvertedDataComparer(Payment paymentData,
                                                Payment paymentDataInverse) {

        return merchantAccountIdComparer(paymentData.getMerchantAccountId(),
                                         paymentDataInverse.getMerchantAccountId())
            && paymentData.getRequestId().equals(paymentDataInverse.getRequestId())
            && paymentData.getTransactionType().equals(paymentDataInverse.getTransactionType())
            && comparerRequestAmount(paymentData.getRequestedAmount(),
                                     paymentDataInverse.getRequestedAmount())
            && accountHolderComparer(paymentData.getAccountHolder(),
                                     paymentDataInverse.getAccountHolder())
            && shippingComparer(paymentData.getShipping(), paymentDataInverse.getShipping())
            && paymentData.getOrderNumber().equals(paymentDataInverse.getOrderNumber())
            && paymentData.getDescriptor().equals(paymentDataInverse.getDescriptor())
            && comparerPaymentMethods(paymentData.getPaymentMethods(),
                                      paymentDataInverse.getPaymentMethods())
            && paymentData.getSuccessRedirectUrl()
                          .equals(paymentDataInverse.getSuccessRedirectUrl())
            && paymentData.getCancelRedirectUrl().equals(paymentDataInverse.getCancelRedirectUrl());

    }

    public boolean merchantAccountIdComparer(MerchantAccountId merchantAccountId, MerchantAccountId merchantAccountIdInverse) {
        return merchantAccountId.getValue().equals(merchantAccountIdInverse.getValue());
    }

    private boolean comparerPaymentMethods(com.wirecard.hybris.core.data.types.PaymentMethods paymentMethods,
                                           com.wirecard.hybris.core.data.types.PaymentMethods paymentMethodsInverse) {

        return comparerPaymentMethod(paymentMethods.getPaymentMethod(), paymentMethodsInverse.getPaymentMethod());
    }

    private boolean comparerPaymentMethod(List<com.wirecard.hybris.core.data.types.PaymentMethod> paymentMethod,
                                          List<com.wirecard.hybris.core.data.types.PaymentMethod> paymentMethodInverse) {
        if (paymentMethod.size() == paymentMethodInverse.size()) {
            if (paymentMethod.isEmpty()) {
                return true;
            }
            for (com.wirecard.hybris.core.data.types.PaymentMethod pym : paymentMethod) {
                for (com.wirecard.hybris.core.data.types.PaymentMethod pymI : paymentMethodInverse) {
                    if (pym.getName().equals(pymI.getName())) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private boolean comparerRequestAmount(Money requestedAmount, Money requestedAmountInverse) {

        return (requestedAmount.getCurrency().equals(requestedAmountInverse.getCurrency())
            && requestedAmount.getValue().equals(requestedAmountInverse.getValue()));

    }

    private boolean accountHolderComparer(com.wirecard.hybris.core.data.types.AccountHolder accountHolder,
                                          com.wirecard.hybris.core.data.types.AccountHolder accountHolderInverse) {

        return accountHolder.getFirstName().equals(accountHolderInverse.getFirstName())
            && accountHolder.getLastName().equals(accountHolderInverse.getLastName());
    }

    private boolean shippingComparer(com.wirecard.hybris.core.data.types.Shipping shipping,
                                     com.wirecard.hybris.core.data.types.Shipping shippingInverse) {

        return shipping.getFirstName().equals(shippingInverse.getFirstName())
            && shipping.getLastName().equals(shippingInverse.getLastName())
            && shipping.getPhone().equals(shippingInverse.getPhone())
            && addressComparer(shipping.getAddress(), shippingInverse.getAddress());
    }

    private boolean addressComparer(com.wirecard.hybris.core.data.types.Address address,
                                    com.wirecard.hybris.core.data.types.Address addressInverse) {

        return address.getCity().equals(addressInverse.getCity())
            && address.getCountry().equals(addressInverse.getCountry())
            && address.getPostalCode().equals(addressInverse.getPostalCode())
            && address.getStreet1().equals(addressInverse.getStreet1());
    }


}
