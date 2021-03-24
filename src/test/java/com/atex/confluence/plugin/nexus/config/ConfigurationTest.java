package com.atex.confluence.plugin.nexus.config;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

    Configuration target;
    @Before
    public void setUp() throws Exception {
        target = new Configuration("http://urlString.com", 
                "username","password","groupId", true, "", true);
    }

    @Test
    public void testConfiguration() {
        Configuration configuration = new Configuration();
        assertNotNull(configuration);
    }

    @Test
    public void testGetUsername() {
        target.setUsername("otheruser");
        assertEquals("otheruser", target.getUsername());
    }

    @Test
    public void testGetPassword() {
        target.setPassword("otherpassword");
        assertEquals("otherpassword", target.getPassword());
    }

    @Test
    public void testGetGroupId() {
        target.setGroupId("othergroupId");
        assertEquals("othergroupId", target.getGroupId());
    }

    @Test
    public void testGetURL() {
        assertEquals("http://urlString.com", target.getURL());
    }

    @Test
    public void testGetURLInvalidUrl() {
        try {
            target.setURL("invalid");
        } catch (MalformedURLException e) {
            // do nothing, will sure be exception
        }
        assertEquals("invalid", target.getURL());
    }

    @Test
    public void testGetURLString() {
        target.setUrlString("http://urlStringToo.com");
        assertEquals("http://urlStringToo.com", target.getUrlString());
    }

    @Test
    public void testGetURLStringInvalid() {
        try {
            target.setURL("invalid");
        } catch (MalformedURLException e) {
            // do nothing, will sure be exception
        }
        assertEquals("invalid", target.getUrlString());
    }

    @Test
    public void testGenerateLink() {
        target.setGenerateLink(false);
        assertFalse(target.isGenerateLink());
    }

    @Test
    public void testGetCredential() {
        UsernamePasswordCredentials credentials = (UsernamePasswordCredentials) target.getCredentials();
        assertNotNull(credentials);
        assertEquals("username", credentials.getUserName());
        assertEquals("password", credentials.getPassword());
    }

    @Test
    public void testGetAuthScope() {
        AuthScope scope = target.getAuthScope();
        assertEquals("urlstring.com", scope.getHost());
        assertEquals(80, scope.getPort());
    }

    @Test
    public void testGetAuthScopeDefinedPort() throws MalformedURLException {
        target.setURL("http://urlString.com:8080");
        AuthScope scope = target.getAuthScope();
        assertEquals("urlstring.com", scope.getHost());
        assertEquals(8080, scope.getPort());
    }

    @Test
    public void testGetNexusLinkPrefix() {
        target.setNexusLinkPrefix("http://nexuslink.com/prefix");
        assertEquals("http://nexuslink.com/prefix", target.getNexusLinkPrefix());
    }

    @Test
    public void testNexus3() {
        assertTrue(target.isNexus3());
        target.setNexus3(false);
        assertFalse(target.isNexus3());
    }

}
