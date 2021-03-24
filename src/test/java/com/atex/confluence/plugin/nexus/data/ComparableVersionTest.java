package com.atex.confluence.plugin.nexus.data;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * ComparableVersionTest
 *
 * Copied from https://octopus.com/blog/maven-versioning-explained
 *
 * @author mnova
 */
public class ComparableVersionTest {

    private static final ComparableVersion[] VERSIONS = new ComparableVersion[]{
            new ComparableVersion("NotAVersionSting"),
            new ComparableVersion("1.0a1-SNAPSHOT"),
            new ComparableVersion("1.0-alpha1"),
            new ComparableVersion("1.0beta1-SNAPSHOT"),
            new ComparableVersion("1.0-b2"),
            new ComparableVersion("1.0-beta3.SNAPSHOT"),
            new ComparableVersion("1.0-beta3"),
            new ComparableVersion("1.0-milestone1-SNAPSHOT"),
            new ComparableVersion("1.0-m2"),
            new ComparableVersion("1.0-rc1-SNAPSHOT"),
            new ComparableVersion("1.0-cr1"),
            new ComparableVersion("1.0-SNAPSHOT"),
            new ComparableVersion("1.0"),
            new ComparableVersion("1.0-RELEASE"),
            new ComparableVersion("1.0-sp"),
            new ComparableVersion("1.0-a"),
            new ComparableVersion("1.0-whatever"),
            new ComparableVersion("1.0.z"),
            new ComparableVersion("1.0.1"),
            new ComparableVersion("1.0.1.0.0.0.0.0.0.0.0.0.0.0.1")
    };

    @Test
    public void ensureArrayInOrder() {
        ComparableVersion[] sortedArray = VERSIONS.clone();
        Arrays.sort(sortedArray);
        Assert.assertArrayEquals(VERSIONS, sortedArray);
    }

    @Test
    public void testAliases() {
        Assert.assertEquals(new ComparableVersion("1.0-alpha1"), new ComparableVersion("1.0-a1"));
        Assert.assertEquals(new ComparableVersion("1.0-beta1"), new ComparableVersion("1.0-b1"));
        Assert.assertEquals(new ComparableVersion("1.0-milestone1"), new ComparableVersion("1.0-m1"));
        Assert.assertEquals(new ComparableVersion("1.0-rc1"), new ComparableVersion("1.0-cr1"));
    }

    @Test
    public void testDifferentFinalReleases() {
        Assert.assertEquals(new ComparableVersion("1.0-ga"), new ComparableVersion("1.0"));
        Assert.assertEquals(new ComparableVersion("1.0-final"), new ComparableVersion("1.0"));
    }

    @Test
    public void testCase() {
        Assert.assertEquals(new ComparableVersion("1.0ALPHA1"), new ComparableVersion("1.0-a1"));
        Assert.assertEquals(new ComparableVersion("1.0Alpha1"), new ComparableVersion("1.0-a1"));
        Assert.assertEquals(new ComparableVersion("1.0AlphA1"), new ComparableVersion("1.0-a1"));
        Assert.assertEquals(new ComparableVersion("1.0BETA1"), new ComparableVersion("1.0-b1"));
        Assert.assertEquals(new ComparableVersion("1.0MILESTONE1"), new ComparableVersion("1.0-m1"));
        Assert.assertEquals(new ComparableVersion("1.0RC1"), new ComparableVersion("1.0-cr1"));
        Assert.assertEquals(new ComparableVersion("1.0GA"), new ComparableVersion("1.0"));
        Assert.assertEquals(new ComparableVersion("1.0FINAL"), new ComparableVersion("1.0"));
        Assert.assertEquals(new ComparableVersion("1.0-SNAPSHOT"), new ComparableVersion("1-snapshot"));
    }

    @Test
    public void testQualifierOnly() {
        Assert.assertTrue(new ComparableVersion("SomeRandomVersionOne").compareTo(
                new ComparableVersion("SOMERANDOMVERSIONTWO")) < 0);
        Assert.assertTrue(new ComparableVersion("SomeRandomVersionThree").compareTo(
                new ComparableVersion("SOMERANDOMVERSIONTWO")) < 0);
    }

    @Test
    public void testSeparators() {
        Assert.assertEquals(new ComparableVersion("1.0alpha1"), new ComparableVersion("1.0-a1"));
        Assert.assertEquals(new ComparableVersion("1.0alpha-1"), new ComparableVersion("1.0-a1"));
        Assert.assertEquals(new ComparableVersion("1.0beta1"), new ComparableVersion("1.0-b1"));
        Assert.assertEquals(new ComparableVersion("1.0beta-1"), new ComparableVersion("1.0-b1"));
        Assert.assertEquals(new ComparableVersion("1.0milestone1"), new ComparableVersion("1.0-m1"));
        Assert.assertEquals(new ComparableVersion("1.0milestone-1"), new ComparableVersion("1.0-m1"));
        Assert.assertEquals(new ComparableVersion("1.0rc1"), new ComparableVersion("1.0-cr1"));
        Assert.assertEquals(new ComparableVersion("1.0rc-1"), new ComparableVersion("1.0-cr1"));
        Assert.assertEquals(new ComparableVersion("1.0ga"), new ComparableVersion("1.0"));
    }

    @Test
    public void testUnequalSeparators() {
        Assert.assertNotEquals(new ComparableVersion("1.0alpha.1"), new ComparableVersion("1.0-a1"));
    }

    @Test
    public void testDashAndPeriod() {
        Assert.assertEquals(new ComparableVersion("1-0.ga"), new ComparableVersion("1.0"));
        Assert.assertEquals(new ComparableVersion("1.0-final"), new ComparableVersion("1.0"));
        Assert.assertEquals(new ComparableVersion("1-0-ga"), new ComparableVersion("1.0"));
        Assert.assertEquals(new ComparableVersion("1-0-final"), new ComparableVersion("1-0"));
        Assert.assertEquals(new ComparableVersion("1-0"), new ComparableVersion("1.0"));
    }

    @Test
    public void testLongVersions() {
        Assert.assertEquals(new ComparableVersion("1.0.0.0.0.0.0"), new ComparableVersion("1"));
        Assert.assertEquals(new ComparableVersion("1.0.0.0.0.0.0x"), new ComparableVersion("1x"));
    }
}