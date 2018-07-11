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
package com.wirecard.hybris.core.converter.xml.impl;

import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.exception.WirecardInvalidSignatureException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@UnitTest
@RunWith(Parameterized.class)
public class PaymentConverterSignatureTest {

    private static Collection<Object[]> data;

    private static final Logger LOG = LoggerFactory.getLogger(PaymentConverterSignatureTest.class);

    static {
        data = new ArrayList<>();
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/test/signature/*.xml");
            // valid certificate
            for (Resource r : resources) {
                boolean valid = r.getFilename().startsWith("valid");
                String xml = IOUtils.toString(r.getInputStream());
                data.add(getTestdata(xml, valid, "/test/signature/api-test.wirecard.com.crt", r.getFilename()));
            }

            // no certificate everything has to be validated to true
            for (Resource r : resources) {
                boolean valid = true;
                String xml = IOUtils.toString(r.getInputStream());
                data.add(getTestdata(xml, valid, null, r.getFilename()));
            }

            // no certificate in file all have to be declined
            for (Resource r : resources) {
                boolean valid = false;
                String xml = IOUtils.toString(r.getInputStream());
                data.add(getTestdata(xml, valid, "/test/signature/no.crt", r.getFilename()));
            }

            // invalid certificate all have to be declined
            for (Resource r : resources) {
                boolean valid = false;
                String xml = IOUtils.toString(r.getInputStream());
                data.add(getTestdata(xml, valid, "/test/signature/invalid.crt", r.getFilename()));
            }
        } catch (Exception e) {
            LOG.info("Init failded", e);
        }
    }

    static Object[] getTestdata(String xml, boolean expected, String certificateFile, String filename) {
        return new Object[]{xml, expected, certificateFile, filename};
    }

    @Parameters
    public static Collection<Object[]> data() {
        return data;
    }


    private DefaultWirecardPaymentConverter paymentConverter = new DefaultWirecardPaymentConverter();

    @Mock
    private MediaService mediaService;

    @Mock
    private BaseStoreService baseStoreService;

    private String xml;
    private boolean valid;
    private String filename;

    private String certificateFile;

    public PaymentConverterSignatureTest(String xml, boolean valid, String certificateFile, String filename) {
        super();
        this.xml = xml;
        this.valid = valid;
        this.filename = filename;
        this.certificateFile = certificateFile;
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        CatalogUnawareMediaModel media = Mockito.mock(CatalogUnawareMediaModel.class);
        BaseStoreModel baseStore = Mockito.mock(BaseStoreModel.class);
        Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);

        if (certificateFile != null) {
            Mockito.when(baseStore.getWirecardCertificate()).thenReturn(media);
            Mockito.when(mediaService.getStreamFromMedia(media))
                   .thenReturn(PaymentConverterSignatureTest.class.getResourceAsStream(certificateFile));
            Mockito.when(mediaService.hasData(media)).thenReturn(true);
        }
        paymentConverter.setBaseStoreService(baseStoreService);
        paymentConverter.setMediaService(mediaService);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testHasValidSignature() throws SAXParseException, WirecardPaymenException {
        boolean valid;
        try {
            valid = paymentConverter.hasValidSignature(xml);
        } catch (WirecardInvalidSignatureException e) {
            valid = false;
        }
        assertEquals("Signature validation failed for file " + filename, this.valid, valid);
    }

    @Test
    public void testConvertXmlToDataWithSignatureCheck() throws SAXParseException {
        boolean valid = false;
        boolean parsed = true;
        try {
            Payment payment = paymentConverter.convertXMLToData(xml, true);
            assertNotNull(payment);
            valid = true;
        } catch (WirecardInvalidSignatureException e) {
            valid = false;
        } catch (WirecardPaymenException e) {
            // some files can not be parsed the should be ignored here
            parsed = false;
        }

        if (parsed) {
            assertEquals("Signature validation failed for file " + filename, this.valid, valid);
        }
    }

    @Test
    public void testConvertXmlToDataWithoutSignatureCheck() throws SAXParseException {
        boolean valid = false;
        boolean parsed = true;
        try {
            Payment payment = paymentConverter.convertXMLToData(xml, false);
            assertNotNull(payment);
            valid = true;
        } catch (WirecardInvalidSignatureException e) {
            valid = false;
        } catch (WirecardPaymenException e) {
            // some files can not be parsed they should be ignored here
            parsed = false;
        }

        if (parsed) {
            assertTrue("Signature validation failed for file " + filename, valid);
        }
    }

}
