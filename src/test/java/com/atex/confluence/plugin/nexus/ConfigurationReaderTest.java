package com.atex.confluence.plugin.nexus;

import java.io.IOException;

import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.junit.Test;
import static junit.framework.Assert.*;

/**
 * @author pau
 *
 */
public class ConfigurationReaderTest {
    
    @Test
    public void testRead() throws IOException {
        Configuration target = new Configuration("http://thisisnexus.com/nexus", "thisisusername", "thisispassword", "com.example.plugins");
        AuthScope scope = target.getAuthScope();
        assertEquals("thisisnexus.com", scope.getHost());
        assertEquals(80, scope.getPort());
        UsernamePasswordCredentials credentials = (UsernamePasswordCredentials)target.getCredentials();
        assertEquals("thisisusername", credentials.getUserName());
        assertEquals("thisispassword", credentials.getPassword());
        assertEquals("com.example.plugins", target.getGroupId());
        assertEquals("http://thisisnexus.com/nexus", target.getURL());
        assertEquals("http://thisisnexus.com/nexus/service/local/lucene/search", target.getSearchURI());
        assertEquals("http://thisisnexus.com/nexus/service/local/all_repositories", target.getSearchRepositoriesURI());
    }
}