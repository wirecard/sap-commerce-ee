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

package com.wirecard.hybris.core.interceptors;

import com.wirecard.hybris.core.model.WirecardAuthenticationModel;
import com.wirecard.hybris.core.service.WirecardPaymentConfigurationService;
import com.wirecard.hybris.core.service.impl.DefaultWirecardPaymentCommandService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@UnitTest
@RunWith(Parameterized.class)
public class DefaultWirecardAuthenticationValidateInterceptorTest {

    private static final String CHECK_AUTHENTICATION_URL = "checkAuthenticationURL";
    private static final String DEFAULT_URL = "defaultURL";
    private static final String PAYPAL = "paypal";

    private static Collection<Object[]> data;

    static {
        data = new ArrayList<>();
        data.add(getTestdata(HttpStatus.SC_NOT_FOUND, true)); // response not found
        data.add(getTestdata(HttpStatus.SC_METHOD_NOT_ALLOWED, true)); // response not allowed
        data.add(getTestdata(HttpStatus.SC_OK, false)); // response ok
    }

    private static Object[] getTestdata(int httpStatus, boolean expected) {
        return new Object[]{httpStatus, expected};
    }

    @Parameters
    public static Collection<Object[]> data() {
        return data;
    }

    public DefaultWirecardAuthenticationValidateInterceptorTest(int response, Boolean expected) {
        this.response = response;
        this.expected = expected;
    }

    @InjectMocks
    private DefaultWirecardAuthenticationValidateInterceptor defaultWirecardAuthenticationValidateInterceptor;

    @Mock
    private DefaultWirecardPaymentCommandService wirecardPaymentCommandService;
    @Mock
    private WirecardPaymentConfigurationService wirecardPaymentConfigurationService;
    @Mock
    private InterceptorContext interceptorContext;

    private WirecardAuthenticationModel authentication;
    private int response;
    private boolean expected;
    private String url;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        authentication = new WirecardAuthenticationModel();
        authentication.setBaseUrl(DEFAULT_URL);
        authentication.setCode(PAYPAL);
        url = DEFAULT_URL + CHECK_AUTHENTICATION_URL;

        Mockito.when(interceptorContext.isNew(authentication)).thenReturn(true);
        Mockito.when(wirecardPaymentConfigurationService.getCheckAuthenticationURL()).thenReturn(CHECK_AUTHENTICATION_URL);
        Mockito.when(wirecardPaymentCommandService.sendTestAuthenticationRequest(url, authentication))
               .thenReturn(response);
    }

    @Test
    public void test() {
        boolean validated;

        try {
            defaultWirecardAuthenticationValidateInterceptor.onValidate(authentication, interceptorContext);
            validated = true;
        } catch (InterceptorException e) {
            validated = false;
        }

        assertEquals(expected, validated);
    }
}
