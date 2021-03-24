package com.atex.confluence.plugin.nexus.config;

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
    private String nexusLinkPrefix;
    private URL url;
    private boolean nexus3 = false;

    public static final String NAME_SPACE = Configuration.class.getName() + ".";
    public static final String USERNAME = NAME_SPACE + "username";
    public static final String PASSWORD = NAME_SPACE + "password";
    public static final String URL = NAME_SPACE + "urlString";
    public static final String GROUPID = NAME_SPACE + "groupId";
    public static final String NEXUS3 = NAME_SPACE + "nexus3";
    public static final String GENERATE_LINK = NAME_SPACE + "generateLink";
    public static final String NEXUSLINKPREFIX = NAME_SPACE + "nexusLinkPrefix";
    
    public Configuration() {
    }
    
    public Configuration(final String urlString,
                         final String username,
                         final String password,
                         final String groupId,
                         final boolean generateLink,
                         final String nexusLinkPrefix,
                         final boolean nexus3) throws MalformedURLException {
        this.urlString = urlString;
        this.username = username;
        this.password = password;
        this.groupId = groupId;
        this.url = toURL(urlString);
        this.generateLink = generateLink;
        this.nexusLinkPrefix = nexusLinkPrefix;
        this.nexus3 = nexus3;
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

    /**
     * @return the nexusLinkPrefix
     */
    public String getNexusLinkPrefix() {
        return nexusLinkPrefix;
    }

    /**
     * @param nexusLinkPrefix the prefix used by maven site and repositories to set
     */
    public void setNexusLinkPrefix(String nexusLinkPrefix) {
        this.nexusLinkPrefix = nexusLinkPrefix;
    }

    public boolean isGenerateLink() {
        return generateLink;
    }

    public void setGenerateLink(boolean generateLink) {
        this.generateLink = generateLink;
    }

    public boolean isNexus3() {
        return nexus3;
    }

    public void setNexus3(final boolean nexus3) {
        this.nexus3 = nexus3;
    }

    public String getSearchURI() {
        if (nexus3) {
            return getURL() + "/service/rest/v1/search/assets";
        } else {
            return getURL() + "/service/local/lucene/search";
        }
    }
    
    public String getSearchRepositoriesURI() {
        if (nexus3) {
            return getURL() + "/service/rest/v1/repositories";
        } else {
            return getURL() + "/service/local/all_repositories";
        }
    }
    
    private URL toURL(final String url) throws MalformedURLException {
        return new URL(url);
    }
}
