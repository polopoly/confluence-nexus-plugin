package com.atex.confluence.plugin.nexus.config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

/**
 * @author pau
 *
 */
public class ConfigurationReaderTest {

    ConfigurationReader target;

    Configuration configuration;

    @Mock
    PluginSettingsFactory pluginSettingsFactory;
    @Mock
    PluginSettings settings;

    private static final String SAMPLE_HOST = "thisisnexus.com";
    private static final String SAMPLE_URL = "http://" + SAMPLE_HOST + "/nexus";
    private static final String SAMPLE_USERNAME = "thisisusername";
    private static final String SAMPLE_PASSWORD = "thisispassword";
    private static final String SAMPLE_GROUPID = "com.example.plugins";
    private static final String SAMPLE_NEXUSLINKPREFIX = "http://nexuslink.com/prefix";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        target = new ConfigurationReader(pluginSettingsFactory);
        when(pluginSettingsFactory.createGlobalSettings()).thenReturn(settings);
        when(settings.get(Configuration.URL)).thenReturn(SAMPLE_URL);
        when(settings.get(Configuration.USERNAME)).thenReturn(SAMPLE_USERNAME);
        when(settings.get(Configuration.PASSWORD)).thenReturn(SAMPLE_PASSWORD);
        when(settings.get(Configuration.GROUPID)).thenReturn(SAMPLE_GROUPID);
        when(settings.get(Configuration.NEXUSLINKPREFIX)).thenReturn(SAMPLE_NEXUSLINKPREFIX);
    }

    @Test
    public void testTransaction() throws MalformedURLException {
        when(settings.get(Configuration.GENERATE_LINK)).thenReturn("true");
        configuration = target.doInTransaction();
        AuthScope scope = configuration.getAuthScope();
        assertEquals(SAMPLE_HOST, scope.getHost());
        assertEquals(80, scope.getPort());
        UsernamePasswordCredentials credentials = (UsernamePasswordCredentials)configuration.getCredentials();
        assertEquals(SAMPLE_USERNAME, credentials.getUserName());
        assertEquals(SAMPLE_PASSWORD, credentials.getPassword());
        assertEquals(SAMPLE_GROUPID, configuration.getGroupId());
        assertEquals(SAMPLE_URL, configuration.getURL());
        assertEquals(SAMPLE_URL + "/service/local/lucene/search", configuration.getSearchURI());
        assertEquals(SAMPLE_URL + "/service/local/all_repositories", configuration.getSearchRepositoriesURI());
        assertEquals(SAMPLE_NEXUSLINKPREFIX, configuration.getNexusLinkPrefix());
        assertEquals(true, configuration.isGenerateLink());
    }

    @Test
    public void testMalformedURLTransaction() throws MalformedURLException {
        when(settings.get(Configuration.URL)).thenReturn("malformed");
        configuration = target.doInTransaction();
        assertNull(configuration);
    }

}