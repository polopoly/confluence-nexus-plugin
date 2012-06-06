package com.atex.confluence.plugin.nexus.data;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class ArtifactTest {

    Artifact target;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        target = spy(new Artifact()) ;
        target.setGroupId("groupId");
        target.setArtifactId("artifactId");
        target.setVersion("1.0.0");
        target.setLatestRelease("2.0.0");
        target.setLatestReleaseRepositoryId("latestReleaseRepositoryId");
        target.setLatestSnapshot("2.0.1-SNAPSHOT");
        target.setLatestSnapshotRepositoryId("latestSnapshotRepositoryId");
//        when(target.isSnapshot()).thenReturn(true);
    }

//    private void whenNotSnapshot() {
//        when(target.isSnapshot()).thenReturn(false);
//    }

    @Test
    public void testGetGroupId() {
        assertEquals("groupId", target.getGroupId());
    }

    @Test
    public void testGetArtifactId() {
        assertEquals("artifactId", target.getArtifactId());
    }

    @Test
    public void testGetVersion() {
        assertEquals("1.0.0", target.getVersion());
    }

    @Test
    public void testGetLatestSnapshot() {
        assertEquals("2.0.1-SNAPSHOT", target.getLatestSnapshot());
    }

    @Test
    public void testGetLatestRelease() {
        assertEquals("2.0.0", target.getLatestRelease());
    }

    @Test
    public void testGetLatestReleaseRepositoryId() {
        assertEquals("latestReleaseRepositoryId", target.getLatestReleaseRepositoryId());
    }

    @Test
    public void testGetLatestSnapshotRepositoryId() {
        assertEquals("latestSnapshotRepositoryId", target.getLatestSnapshotRepositoryId());
    }

    @Test
    public void testIsSnapshot() {
        target.setVersion("2.0.1-SNAPSHOT");
        assertTrue(target.isSnapshot());
    }

    @Test
    public void testIsNotSnapShot() {
        assertFalse(target.isSnapshot());
    }

    @Test
    public void testIsSnapshotNullVersion() {
        target.setVersion(null);
        assertFalse(target.isSnapshot());
    }

}
