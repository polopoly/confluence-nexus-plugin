package com.atex.confluence.plugin.nexus.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ArtifactTest {

    Artifact target;

    @Before
    public void setUp() throws Exception {
        target = new Artifact() ;
        target.setGroupId("groupId");
        target.setArtifactId("artifactId");
        target.setVersion("1.0.0");
        target.setLatestRelease("2.0.0");
        target.setLatestReleaseRepositoryId("latestReleaseRepositoryId");
        target.setLatestSnapshot("2.0.1-SNAPSHOT");
        target.setLatestSnapshotRepositoryId("latestSnapshotRepositoryId");
    }

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

    @Test
    public void testToString() {
        String expected = "[groupId: groupId, artifactId: artifactId, version: 1.0.0, latestSnapshotRepositoryId: latestSnapshotRepositoryId, latestReleaseRepositoryId: latestReleaseRepositoryId, latestRelease: 2.0.0]";
        assertEquals(expected, target.toString());
    }

    @Test
    public void testEqual() {
        assertTrue(target.equals(target));
    }

    @Test
    public void testNotEqualArtifactId() {
        Artifact another = new Artifact() ;
        another.setGroupId("groupId");
        another.setArtifactId("otherartifactId");
        another.setVersion("1.0.0");
        assertFalse(target.equals(another));
    }

    @Test
    public void testNotEqualGroupId() {
        Artifact another = new Artifact() ;
        another.setGroupId("othergroupId");
        another.setArtifactId("artifactId");
        another.setVersion("1.0.0");
        assertFalse(target.equals(another));
    }

    @Test
    public void testNotEqualVersion() {
        Artifact another = new Artifact() ;
        another.setGroupId("groupId");
        another.setArtifactId("artifactId");
        another.setVersion("2.0.0");
        assertFalse(target.equals(another));
    }

    @Test
    public void testNotEqualOtherInstance() {
        Object object = new Object();
        assertFalse(target.equals(object));
    }

    @Test
    public void testHashCode() {
        Integer expected = target.hashCode();
        assertNotNull(expected);
    }
}
