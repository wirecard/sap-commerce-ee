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

import com.google.common.collect.ImmutableList;
import com.wirecard.hybris.core.converter.xml.PaymentConverter;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.exception.WirecardInvalidSignatureException;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DefaultWirecardPaymentConverter implements PaymentConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWirecardPaymentConverter.class);

    private List<String> xmlExternalFeatures = ImmutableList.of(
            "http://xml.org/sax/features/external-general-entities",
            "http://xml.org/sax/features/external-parameter-entities",
            "http://apache.org/xml/features/nonvalidating/load-external-dtd");

    private BaseStoreService baseStoreService;

    private MediaService mediaService;

    @Override
    public String convertDataToXML(Payment paymentData) throws WirecardPaymenException {
        try {

            StringWriter xmlStringWriter = new StringWriter();
            JAXBContext contextClass = JAXBContext.newInstance(Payment.class);
            Marshaller paymentMarshaller = contextClass.createMarshaller();
            paymentMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            paymentMarshaller.marshal(paymentData, xmlStringWriter);
            return xmlStringWriter.toString();

        } catch (JAXBException e) {
            throw new WirecardPaymenException("Conversion of data to xml failed", e);
        }
    }

    @Override
    public Payment convertXMLToData(String xml) throws WirecardPaymenException {
        return convertXMLToData(xml, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Payment convertXMLToData(String xml, boolean validateSignature)
            throws WirecardPaymenException {
        if (validateSignature && !hasValidSignature(xml)) {
            throw new WirecardInvalidSignatureException("Signature of passed document was not valid");
        }

        try {
            StringReader xmlStringReader = new StringReader(xml);
            JAXBContext contextClass = JAXBContext.newInstance(Payment.class);
            Unmarshaller paymentUnmarshaller = contextClass.createUnmarshaller();
            return (Payment) paymentUnmarshaller.unmarshal(xmlStringReader);
        } catch (JAXBException e) {
            throw new WirecardPaymenException("Conversion of passed document failed", e);
        }
    }

    protected boolean hasValidSignature(String xml) throws WirecardInvalidSignatureException {
        BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
        MediaModel certificate = baseStore.getWirecardCertificate();

        if (certificate != null && mediaService.hasData(certificate)) {
            return hasValidSignature(xml, mediaService.getStreamFromMedia(certificate));
        } else {
            if (LOG.isInfoEnabled()) {
                LOG.info("No certificate was found in base store {}. Hence, all signatures are treated as valid.", baseStore.getName());
            }
            return true;
        }
    }

    protected boolean hasValidSignature(String xml, InputStream certificate) throws WirecardInvalidSignatureException {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(certificate);
            return hasValidSignature(xml, x509Certificate);
        } catch (CertificateException e) {
            throw new WirecardInvalidSignatureException("Signature validation failed", e);
        }
    }

    protected boolean hasValidSignature(String xml, X509Certificate certificate)
        throws CertificateException, WirecardInvalidSignatureException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Signature validation with certificate {}", certificate.getSerialNumber());
        }
        // Instantiate the document to be signed.
        try {
            certificate.checkValidity();
            Document doc = newDocumentBuilder().parse(IOUtils.toInputStream(xml, StandardCharsets.UTF_8));
            NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
            if (nl.getLength() == 0) {
                throw new XMLSignatureException("Cannot find Signature element");
            }
            // Create a DOMValidateContext and specify a KeySelector
            // and document context.
            DOMValidateContext valContext = new DOMValidateContext(certificate.getPublicKey(), nl.item(0));

            // Unmarshal the XMLSignature
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);

            // Validate the XMLSignature.
            return signature.validate(valContext);
        } catch (NullPointerException | SAXException | IOException | ParserConfigurationException | MarshalException
            | XMLSignatureException e) {
            throw new WirecardInvalidSignatureException("Signature could not be verified", e);
        }
    }

    protected DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);

        for (String feature : xmlExternalFeatures) {
            dbf.setFeature(feature, false);
        }

        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return dbf.newDocumentBuilder();
    }

    protected BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    protected MediaService getMediaService() {
        return mediaService;
    }

    @Required
    public void setMediaService(MediaService mediaService) {
        this.mediaService = mediaService;
    }

}
