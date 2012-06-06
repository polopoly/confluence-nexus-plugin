package com.atex.confluence.plugin.nexus.connect;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class RepositoryTest {

    Repository target;

    @Before
    public void setUp() throws Exception {
        target = new Repository() ;
        target.setRepositoryId("repositoryId");
        target.setRepositoryURL("repositoryURL");
    }

    @Test
    public void testGetRepositoryId() {
        assertEquals("repositoryId", target.getRepositoryId());
    }

    @Test
    public void testGetRepositoryURL() {
        assertEquals("repositoryURL", target.getRepositoryURL());
    }

}
