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
    private boolean generateLink = false;
    private URL url;
    
    public static final String NAME_SPACE = Configuration.class.getName() + ".";
    public static final String USERNAME = NAME_SPACE + "username";
    public static final String PASSWORD = NAME_SPACE + "password";
    public static final String URL = NAME_SPACE + "urlString";
    public static final String GROUPID = NAME_SPACE + "groupId";
    public static final String GENERATE_LINK = NAME_SPACE + "generateLink";
    
    public Configuration() {
        
    }
    
    public Configuration(String urlString, String username, String password, String groupId, boolean generateLink) throws MalformedURLException {
        this.urlString = urlString;
        this.username = username;
        this.password = password;
        this.groupId = groupId;
        this.url = toURL(urlString);
        this.generateLink = generateLink;
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
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getURL() {
        return urlString;
    }
    
    public void setURL(String urlString) throws MalformedURLException {
        this.urlString = urlString;
        this.url = toURL(urlString);
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUrlString() {
        return urlString;
    }
    
    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }
    
    public boolean isGenerateLink() {
        return generateLink;
    }
    public void setGenerateLink(boolean generateLink) {
        this.generateLink = generateLink;
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
