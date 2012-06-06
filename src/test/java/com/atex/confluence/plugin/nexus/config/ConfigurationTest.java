package com.atex.confluence.plugin.nexus.config;

import static org.junit.Assert.*;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

public class ConfigurationTest {

    Configuration target;
    @Before
    public void setUp() throws Exception {
        target = new Configuration("http://urlString.com", 
                "username","password","groupId",true);
    }

    @Test
    public void testGetUsername() {
        assertEquals("username", target.getUsername());
    }

    @Test
    public void testGetPassword() {
        assertEquals("password", target.getPassword());
    }

    @Test
    public void testGetGroupId() {
        assertEquals("groupId", target.getGroupId());
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
        assertEquals("http://urlString.com", target.getUrlString());
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


}
