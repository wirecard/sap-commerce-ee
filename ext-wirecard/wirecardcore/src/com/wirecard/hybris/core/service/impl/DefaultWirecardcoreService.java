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
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.io.InputStream;
import java.util.Optional;

public class DefaultWirecardcoreService implements WirecardcoreService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWirecardcoreService.class);

    private static final String FIND_LOGO_QUERY = "SELECT {" + CatalogUnawareMediaModel.PK + "} FROM {"
        + CatalogUnawareMediaModel._TYPECODE + "} WHERE {" + CatalogUnawareMediaModel.CODE + "}=?code";

    private MediaService mediaService;
    private ModelService modelService;
    private FlexibleSearchService flexibleSearchService;

    @Override
    public String getHybrisLogoUrl(final String logoCode) {
        final MediaModel media = mediaService.getMedia(logoCode);

        // Keep in mind that with Slf4j you don't need to check if debug is enabled, it is done under the hood.
        LOG.debug("Found media [code: {}]", media.getCode());

        return media.getURL();
    }

    @Override
    public void createLogo(final String logoCode) {
        final Optional<CatalogUnawareMediaModel> existingLogo = findExistingLogo(logoCode);

        final CatalogUnawareMediaModel media;
        if (existingLogo.isPresent()) {
            media = existingLogo.get();
        } else {
            media = modelService.create(CatalogUnawareMediaModel.class);
        }

        media.setCode(logoCode);
        media.setRealFileName("sap-hybris-platform.png");
        modelService.save(media);

        mediaService.setStreamForMedia(media, getImageStream());
    }

    /**
     * Searches if the logo already exists
     *
     * @param logoCode
     *     Platform Logo
     * @return the CatalogMediaModel of the logo if it exists
     */
    private Optional<CatalogUnawareMediaModel> findExistingLogo(final String logoCode) {
        final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(FIND_LOGO_QUERY);
        fQuery.addQueryParameter("code", logoCode);

        try {
            return Optional.of(flexibleSearchService.searchUnique(fQuery));
        } catch (final SystemException e) {
            LOG.debug("Returning empty", e);
            return Optional.empty();
        }
    }

    /**
     * Retreives Hybris logo image
     *
     * @return Hybris logo
     */
    private InputStream getImageStream() {
        return DefaultWirecardcoreService.class.getResourceAsStream("/wirecardcore/sap-hybris-platform.png");
    }

    @Required
    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @Required
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    @Required
    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
