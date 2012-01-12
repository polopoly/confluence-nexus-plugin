package com.atex.confluence.plugin.nexus;

import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;

/**
 * @author pau
 *
 */
public class Configuration {
    
    private String username;
    private String password;
    private String groupId;
    private String urlString;
    private URL url;
    
    public static final String NAME_SPACE = Configuration.class.getName() + ".";
    public static final String USERNAME = NAME_SPACE + "username";
    public static final String PASSWORD = NAME_SPACE + "password";
    public static final String URL = NAME_SPACE + "urlString";
    public static final String GROUPID = NAME_SPACE + "groupId";
    
    public Configuration() {
        
    }
    
    public Configuration(String urlString, String username, String password, String groupId) throws MalformedURLException {
        this.urlString = urlString;
        this.username = username;
        this.password = password;
        this.groupId = groupId;
        this.url = toURL(urlString);
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
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getUrlString() {
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
