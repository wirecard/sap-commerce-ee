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

package de.hybris.platform.hac.controller;

import de.hybris.platform.core.MasterTenant;
import de.hybris.platform.jalo.extension.Extension;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/wirecardhac/**")
public class WirecardhacController {

    private final String pluginName = "wirecard";
    private final String extensionsAttribute = "extensions";
    private final String propertySearch = "version";
    private final String propertiesFile = "/%s.build.number";
    private final String noPropertyReplacement = "n/a";
    private final String extensionValue = "%s: %s";

    @Value("${hac.extlinks.wiki.hacextensions}")
    private String wikiHacExtensions;

    @RequestMapping(value = "/pluginInfo", method = RequestMethod.GET)
    public String showPluginInfoWirecard(final Model model) {

        List<? extends Extension> listaExtensions = ExtensionManager.getInstance().getExtensions();

        List<String> extensions = listaExtensions.stream()
                                                 .filter(extension -> extension.getName().contains(pluginName))
                                                 .map(extension -> obtainExtensionVersion(extension))
                                                 .collect(Collectors.toList());

        model.addAttribute(extensionsAttribute, extensions);

        return "pluginInfo";
    }

    @RequestMapping(value = "/supportTeams", method = RequestMethod.GET)
    public String showSupportTeamsWirecard(final Model model) {
        return "supportTeams";
    }

    private String obtainExtensionVersion(final Extension extension) {

        String version = null;

        try {
            Properties props = new Properties();
            InputStream input =
                MasterTenant.class.getResourceAsStream(String.format(propertiesFile, extension.getName()));

            if (input != null) {
                props.load(input);
            }

            IOUtils.closeQuietly(input);
            version = props.getProperty(propertySearch);
            version = version == null ? noPropertyReplacement : version;
        } catch (IOException ioe) {
            version = noPropertyReplacement;
        }

        return String.format(extensionValue, extension.getName(), version);
    }
}