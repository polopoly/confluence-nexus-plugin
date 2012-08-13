package com.atex.confluence.plugin.nexus.config;

import static com.atex.confluence.plugin.nexus.config.Configuration.*;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pau
 */
public class ConfigurationReader implements TransactionCallback<Configuration> {

    private PluginSettingsFactory pluginSettingsFactory;
    
    public ConfigurationReader(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }
    
    @Override
    public Configuration doInTransaction() {
        try {
            PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            String urlString = (String)settings.get(URL);
            String username = (String)settings.get(USERNAME);
            String password = (String)settings.get(PASSWORD);
            String groupId = (String)settings.get(GROUPID);
            String nexusLinkPrefix = (String)settings.get(NEXUSLINKPREFIX);
            Boolean generateLink = Boolean.valueOf((String) settings.get(GENERATE_LINK));
            return new Configuration(urlString, username, password, groupId, generateLink == null? false: generateLink.booleanValue(), nexusLinkPrefix);
        } catch (MalformedURLException ex) {
            // invalid url
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }
}
