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

package com.wirecard.hybris.core.setup;

import com.wirecard.hybris.core.constants.WirecardcoreConstants;
import com.wirecard.hybris.core.service.WirecardcoreService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;

import java.util.ArrayList;
import java.util.List;

@SystemSetup(extension = WirecardcoreConstants.EXTENSIONNAME)
public class WirecardcoreSystemSetup extends AbstractSystemSetup {

    private static final String IMPORT_CORE_DATA = "importCoreData";
    private static final String IMPORT_SAMPLE_DATA_ELECTRONICS = "importSampleDataElectronics";
    private static final String IMPORT_SAMPLE_DATA_APPAREL = "importSampleDataApparel";
    private static final String ACTIVATE_PAYMENT_METHOD = "activatePaymentMethod";


    private final WirecardcoreService wirecardcoreService;

    public WirecardcoreSystemSetup(final WirecardcoreService wirecardcoreService) {
        this.wirecardcoreService = wirecardcoreService;
    }

    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        final List<SystemSetupParameter> params = new ArrayList<>();

        params.add(createBooleanSystemSetupParameter(IMPORT_CORE_DATA, "Import Core Data", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA_ELECTRONICS, "Import Sample Data Electronics", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SAMPLE_DATA_APPAREL, "Import Sample Data Apparel-DE", true));
        params.add(createBooleanSystemSetupParameter(ACTIVATE_PAYMENT_METHOD, "Activate All Payment Method", true));

        return params;
    }

    @SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.INIT)
    public void createEssentialData() {
        wirecardcoreService.createLogo(WirecardcoreConstants.PLATFORM_LOGO_CODE);
    }


    @SystemSetup(type = SystemSetup.Type.PROJECT, process = SystemSetup.Process.ALL)
    public void createProjectData(final SystemSetupContext context) {
        if (this.getBooleanSystemSetupParameter(context, IMPORT_CORE_DATA)) {
            importImpexFile(context, "/wirecardcore/import/common-addon-extra.impex");
        }
        if (this.getBooleanSystemSetupParameter(context, IMPORT_SAMPLE_DATA_ELECTRONICS)) {
            importImpexFile(context, "/wirecardcore/import/sampledata/currencies.impex");
            importImpexFile(context, "/wirecardcore/import/sampledata/wd-electronics-sample-data.impex");
        }
        if (this.getBooleanSystemSetupParameter(context, IMPORT_SAMPLE_DATA_APPAREL)) {
            importImpexFile(context, "/wirecardcore/import/sampledata/currencies.impex");
            importImpexFile(context, "/wirecardcore/import/sampledata/wd-apparel-de-sample-data.impex");
        }
        if (this.getBooleanSystemSetupParameter(context, ACTIVATE_PAYMENT_METHOD)) {
            importImpexFile(context, "/wirecardcore/import/sampledata/wd-activate-payments.impex");
        }
    }
}
