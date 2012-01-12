package com.atex.confluence.plugin.nexus;

import static com.atex.confluence.plugin.nexus.Configuration.*;
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
            System.out.println(urlString);
            String username = (String)settings.get(USERNAME);
            System.out.println(username);
            String password = (String)settings.get(PASSWORD);
            System.out.println(password);
            String groupId = (String)settings.get(GROUPID);
            return new Configuration(urlString, username, password, groupId);
        } catch (MalformedURLException ex) {
            // invalid url
            Logger.getLogger(ConfigurationReader.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return null;
    }
}
