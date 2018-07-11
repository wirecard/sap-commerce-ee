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

import com.wirecard.hybris.core.service.WirecardcoreService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static com.wirecard.hybris.core.constants.WirecardcoreConstants.PLATFORM_LOGO_CODE;
import static org.fest.assertions.Assertions.assertThat;

/**
 * This is an example of how the integration test should look like. {@link ServicelayerBaseTest} bootstraps platform so
 * you have an access to all Spring beans as well as database connection. It also ensures proper cleaning out of items
 * created during the test after it finishes. You can inject any Spring service using {@link Resource} annotation. Keep
 * in mind that by default it assumes that annotated field name matches the Spring Bean ID.
 */
@IntegrationTest
public class DefaultWirecardcoreServiceIntegrationTest extends ServicelayerBaseTest {

    @Resource
    private WirecardcoreService wirecardcoreService;
    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Before
    public void setUp() throws Exception {
        wirecardcoreService.createLogo(PLATFORM_LOGO_CODE);
    }

    @Test
    public void shouldReturnProperUrlForLogo() throws Exception {
        // given
        final String logoCode = "wirecardcorePlatformLogo";

        // when
        final String logoUrl = wirecardcoreService.getHybrisLogoUrl(logoCode);

        // then
        assertThat(logoUrl).isNotNull();
        assertThat(logoUrl).isEqualTo(findLogoMedia(logoCode).getURL());
    }

    private MediaModel findLogoMedia(final String logoCode) {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery("SELECT {PK} FROM {Media} WHERE {code}=?code");
        fQuery.addQueryParameter("code", logoCode);

        return flexibleSearchService.searchUnique(fQuery);
    }

}
