package com.atex.confluence.plugin.nexus;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * @author pau
 *
 */
public class Configuration {
    
    private String propertiesPath = "confluence-nexus-config.properties";
    private String password;
    private String username;
    private String groupId;
    private String urlString;
    private URL url;
    
    public Configuration() {
        init();
    }
    
    // only allow package visibility for testing purpose
    Configuration(String propertiesPath) {
        this.propertiesPath = propertiesPath;
        init();
    }
    
    protected void init() {
        InputStream is = getClass().getClassLoader().getResourceAsStream(propertiesPath);
        Properties properties = new Properties();
        try {
            properties.load(is);
            urlString = properties.getProperty("url");
            url = toURL(urlString);
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            groupId = properties.getProperty("groupId");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    public Credentials getCredentials() {
        return new UsernamePasswordCredentials(username, password);
    }
    
    public AuthScope getAuthScope() {
        return new AuthScope(url.getHost(), url.getPort() == -1? 80: url.getPort());
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getURL() {
        return urlString;
    }
    
    public String getSearchURI() {
        return getURL() + "/service/local/lucene/search";
    }
    
    public String getSearchRepositoriesURI() {
        return getURL() + "/service/local/all_repositories";
    }
    
    private URL toURL(String url) throws MalformedURLException {
        URL result = new URL(url);
        return result;
    }
}
