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
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.util.TestUtils;
import com.wirecard.hybris.exception.WirecardInvalidSignatureException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@UnitTest
public class PaypalAuthorizationPaymentResponseConverterTest extends HybrisJUnit4TransactionalTest {

    private static final Logger LOG = Logger.getLogger(PaypalAuthorizationPaymentResponseConverterTest.class);
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
    public void testPayPalResponseConverter() throws WirecardInvalidSignatureException, WirecardPaymenException {

        String xml = generationXML();
        Payment payment = xmlConverter.convertXMLToData(xml);

        String xmlInverse = xmlConverter.convertDataToXML(payment);

        LOG.info(xml);
        LOG.info(xmlInverse);

        assertTrue("The original XML is not equal to the reverse converted XLM, for Pojo to XML",
                   xml.equals(xmlInverse));

    }

    private String generationXML() {

        return TestUtils.getFile("test/response.xml");
    }


}
