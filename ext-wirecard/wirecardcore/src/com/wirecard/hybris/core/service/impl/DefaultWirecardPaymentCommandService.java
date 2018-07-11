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

package com.wirecard.hybris.core.service.impl;

import com.wirecard.hybris.core.converter.xml.PaymentConverter;
import com.wirecard.hybris.core.data.types.Payment;
import com.wirecard.hybris.core.data.types.PaymentMethodName;
import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.service.PaymentCommandService;
import com.wirecard.hybris.exception.WirecardPaymenException;
import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.core.MasterTenant;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

public class DefaultWirecardPaymentCommandService implements PaymentCommandService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWirecardPaymentCommandService.class);

    private static final String CONTENT_TYPE = "application/xml";
    private static final String FINGERPRINT_MATCHES =
        "request {} fingerprint matches with response fingerprint";
    private static final String FINGERPRINT_DO_NOT_MATCH =
        "request fingerprint do not match with response fingerprint for transaction {}";
    private static final String NOT_AVAILABLE = "n/a";

    private BasicResponseHandler basicResponseHandler;
    private PaymentConverter paymentConverter;

    @Override
    public Payment sendRequest(final Payment payment, String url, WirecardAuthenticationModel wirecardAuthenticationModel)
        throws WirecardPaymenException {

        String xmlRequest = getPaymentConverter().convertDataToXML(payment);

        String xmlResponse = getResponse(configureHttpRequest(xmlRequest, url, wirecardAuthenticationModel));

        Payment paymentResponse = getPaymentConverter().convertXMLToData(xmlResponse);

        checkFingerprint(payment, paymentResponse);

        return paymentResponse;
    }

    private void checkFingerprint(Payment payment, Payment paymentResponse) {

        if (payment.getPaymentMethods().getPaymentMethod().get(0).getName()
                   .equals(PaymentMethodName.RATEPAY_INVOICE)
            && payment.getDevice() != null) {

            String requestFingerPrint = payment.getDevice().getFingerprint();

            if (StringUtils.isNotEmpty(requestFingerPrint)) {

                String responseFingerPrint = paymentResponse.getDevice().getFingerprint();

                if (requestFingerPrint.equals(responseFingerPrint)) {
                    LOGGER.debug(FINGERPRINT_MATCHES, payment.getRequestId());
                } else {
                    LOGGER.error(FINGERPRINT_DO_NOT_MATCH, paymentResponse.getTransactionId());
                }
            }
        }
    }

    @Override
    public int sendTestAuthenticationRequest(String url, WirecardAuthenticationModel wirecardAuthenticationModel) {
        return getResponse(configureHttpRequest(url, wirecardAuthenticationModel));
    }

    @Override
    public String sendSearchRequest(String url, WirecardAuthenticationModel wirecardAuthenticationModel) {
        return getSearchResponse(configureHttpRequest(url, wirecardAuthenticationModel));
    }

    protected String getResponse(final HttpPost httppost) {
        String body = StringUtils.EMPTY;
        try (CloseableHttpClient client = HttpClients.createSystem()) {
            final HttpResponse response = client.execute(httppost);
            body = getBasicResponseHandler().handleResponse(response);
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return body;
    }

    protected int getResponse(final HttpGet httpGet) {
        int code = 0;
        try (CloseableHttpClient client = HttpClients.createSystem()) {
            final HttpResponse response = client.execute(httpGet);
            code = response.getStatusLine().getStatusCode();
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return code;
    }

    protected String getSearchResponse(final HttpGet httpGet) {

        String body = StringUtils.EMPTY;

        try (CloseableHttpClient client = HttpClients.createSystem()) {

            HttpResponse response = client.execute(httpGet);
            body = getBasicResponseHandler().handleResponse(response);

        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return body;
    }

    private HttpPost configureHttpRequest(final String xmlRequest,
                                          String url,
                                          WirecardAuthenticationModel wirecardAuthenticationModel) {

        final HttpPost httppost = new HttpPost(url);

        final StringEntity entity = new StringEntity(xmlRequest, ContentType.create(
            "text/xml", Consts.UTF_8));
        entity.setChunked(true);
        httppost.setEntity(entity);

        httppost.addHeader("Content-Type", CONTENT_TYPE);
        httppost.addHeader("Accept", CONTENT_TYPE);
        addPluginInfoHttpRequest(httppost);

        //Authorization
        final StringBuilder credential = new StringBuilder()
            .append(wirecardAuthenticationModel.getUsername())
            .append(':')
            .append(wirecardAuthenticationModel.getPassword());
        final String encodedCredential = Base64.getEncoder().encodeToString(credential.toString().getBytes(StandardCharsets.UTF_8));
        httppost.addHeader("Authorization", "Basic " + encodedCredential);

        return httppost;

    }

    private HttpGet configureHttpRequest(String url, WirecardAuthenticationModel wirecardAuthenticationModel) {

        final HttpGet httpget = new HttpGet(url);

        httpget.addHeader("Content-Type", CONTENT_TYPE);
        httpget.addHeader("Accept", CONTENT_TYPE);
        addPluginInfoHttpRequest(httpget);

        //Check Credentials
        final StringBuilder credential = new StringBuilder()
            .append(wirecardAuthenticationModel.getUsername())
            .append(':')
            .append(wirecardAuthenticationModel.getPassword());
        final String encodedCredential = Base64.getEncoder().encodeToString(credential.toString().getBytes(StandardCharsets.UTF_8));
        httpget.addHeader("Authorization", "Basic " + encodedCredential);

        return httpget;

    }

    private void addPluginInfoHttpRequest(AbstractHttpMessage http) {
        http.addHeader("shop-system-name", "hybris");
        http.addHeader("shop-system-version", obtainSystemVersion());
        http.addHeader("plugin-name", "wirecard");
        http.addHeader("plugin-version", obtainExtensionVersion("/wirecardcore.build.number"));
    }

    private String obtainSystemVersion() {

        String version;

        PlatformConfig config = ConfigUtil.getPlatformConfig(getClass());
        File platformHome = config.getSystemConfig().getPlatformHome();
        File buildNumber = new File(platformHome, "build.number");

        try {
            InputStream input = FileUtils.openInputStream(buildNumber);

            version = obtainVersionInput(input);
        } catch (IOException e) {
            LOGGER.error("Input error", e);
            version = NOT_AVAILABLE;
        }

        return version;
    }

    private String obtainExtensionVersion(final String document) {

        InputStream input =
            MasterTenant.class.getResourceAsStream(document);

        return obtainVersionInput(input);
    }

    private String obtainVersionInput(InputStream input) {

        String result;

        try {
            Properties props = new Properties();

            if (input != null) {
                props.load(input);
            }

            if (input != null) {
                input.close();
            }
            result = props.getProperty("version");
            if (result == null) {
                result = NOT_AVAILABLE;
            }
        } catch (IOException ioe) {
            LOGGER.error("Input error", ioe);
            result = NOT_AVAILABLE;
        }

        return result;
    }

    protected BasicResponseHandler getBasicResponseHandler() {
        return basicResponseHandler;
    }

    @Required
    public void setBasicResponseHandler(BasicResponseHandler basicResponseHandler) {
        this.basicResponseHandler = basicResponseHandler;
    }

    protected PaymentConverter getPaymentConverter() {
        return paymentConverter;
    }

    @Required
    public void setPaymentConverter(PaymentConverter paymentConverter) {
        this.paymentConverter = paymentConverter;
    }

}
