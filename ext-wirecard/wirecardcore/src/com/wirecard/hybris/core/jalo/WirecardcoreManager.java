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

package com.wirecard.hybris.core.jalo;

import de.hybris.platform.core.Registry;
import de.hybris.platform.util.JspContext;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wirecard.hybris.core.constants.WirecardcoreConstants;

/**
 * This is the extension manager of the Wirecardcore extension.
 */
public class WirecardcoreManager extends GeneratedWirecardcoreManager {

    /** Edit the local|project.properties to change logging behavior (properties 'log4j.*'). */
    private static final Logger LOG = LoggerFactory.getLogger(WirecardcoreManager.class);

    /*
     * Some important tips for development: Do NEVER use the default constructor of manager's or items. => If you want
     * to do something whenever the manger is created use the init() or destroy() methods described below Do NEVER use
     * STATIC fields in your manager or items! => If you want to cache anything in a "static" way, use an instance
     * variable in your manager, the manager is created only once in the lifetime of a "deployment" or tenant.
     */

    /**
     * Get the valid instance of this manager.
     * 
     * @return the current instance of this manager
     */
    public static WirecardcoreManager getInstance() {
        return (WirecardcoreManager) Registry.getCurrentTenant().getJaloConnection().getExtensionManager()
                .getExtension(WirecardcoreConstants.EXTENSIONNAME);
    }

    /**
     * Never call the constructor of any manager directly, call getInstance() You can place your business logic here -
     * like registering a jalo session listener. Each manager is created once for each tenant.
     */
    public WirecardcoreManager() // NOPMD
    {
        LOG.debug("constructor of WirecardcoreManager called.");
    }

    /**
     * Use this method to do some basic work only ONCE in the lifetime of a tenant resp. "deployment". This method is
     * called after manager creation (for example within startup of a tenant). Note that if you have more than one
     * tenant you have a manager instance for each tenant.
     */
    @Override
    public void init() {
        LOG.debug("init() of WirecardcoreManager called, current tenant: {}", getTenant().getTenantID());
    }

    /**
     * Use this method as a callback when the manager instance is being destroyed (this happens before system
     * initialization, at redeployment or if you shutdown your VM). Note that if you have more than one tenant you have
     * a manager instance for each tenant.
     */
    @Override
    public void destroy() {
        LOG.debug("destroy() of WirecardcoreManager called, current tenant: {}", getTenant().getTenantID());
    }

    /**
     * Implement this method to create initial objects. This method will be called by system creator during
     * initialization and system update. Be sure that this method can be called repeatedly. An example usage of this
     * method is to create required cronjobs or modifying the type system (setting e.g some default values)
     * 
     * @param params
     *            the parameters provided by user for creation of objects for the extension
     * @param jspc
     *            the jsp context; you can use it to write progress information to the jsp page during creation
     */
    @Override
    public void createEssentialData(final Map<String, String> params, final JspContext jspc) {
        // implement here code creating essential data
    }

    /**
     * Implement this method to create data that is used in your project. This method will be called during the system
     * initialization. An example use is to import initial data like currencies or languages for your project from an
     * csv file.
     * 
     * @param params
     *            the parameters provided by user for creation of objects for the extension
     * @param jspc
     *            the jsp context; you can use it to write progress information to the jsp page during creation
     */
    @Override
    public void createProjectData(final Map<String, String> params, final JspContext jspc) {
        // implement here code creating project data
    }
}
