package com.atex.confluence.plugin.nexus.config;

import static com.atex.confluence.plugin.nexus.config.Configuration.GENERATE_LINK;
import static com.atex.confluence.plugin.nexus.config.Configuration.GROUPID;
import static com.atex.confluence.plugin.nexus.config.Configuration.NEXUS3;
import static com.atex.confluence.plugin.nexus.config.Configuration.NEXUSLINKPREFIX;
import static com.atex.confluence.plugin.nexus.config.Configuration.PASSWORD;
import static com.atex.confluence.plugin.nexus.config.Configuration.URL;
import static com.atex.confluence.plugin.nexus.config.Configuration.USERNAME;

import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionCallback;

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
            final PluginSettings settings = pluginSettingsFactory.createGlobalSettings();
            final String urlString = (String)settings.get(URL);
            final String username = (String)settings.get(USERNAME);
            final String password = (String)settings.get(PASSWORD);
            final String groupId = (String)settings.get(GROUPID);
            final String nexusLinkPrefix = (String)settings.get(NEXUSLINKPREFIX);
            final boolean generateLinkValue = getAsBoolean(settings, GENERATE_LINK);
            final boolean nexus3 = getAsBoolean(settings, NEXUS3);
            return new Configuration(
                    urlString,
                    username,
                    password,
                    groupId,
                    generateLinkValue,
                    nexusLinkPrefix,
                    nexus3);
        } catch (MalformedURLException ex) {
            // invalid url
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }

    private boolean getAsBoolean(final PluginSettings settings,
                                 final String name) {
        final String value = (String) settings.get(name);
        if (value != null) {
            return Boolean.parseBoolean(value);
        }
        return false;
    }

    public PluginSettingsFactory getPluginSettingsFactory() {
        return pluginSettingsFactory;
    }
}
