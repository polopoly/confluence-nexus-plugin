package com.atex.confluence.plugin.nexus.data;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.atex.confluence.plugin.nexus.config.Configuration;
import com.atex.confluence.plugin.nexus.connect.Repository;
import com.atex.confluence.plugin.nexus.util.StopWatch;

/**
 * MetadataManagerTest
 *
 * @author mnova
 */
@Ignore
public class MetadataManagerTest {

    @Ignore
    @Test
    public void testNexus1() throws Exception {
        final Configuration conf = new Configuration(
                "http://maven.polopoly.com/nexus",
                "ppjenkins",
                "dxthlps1",
                "com.atex.plugins&repositoryId=polopoly-plugins",
                true,
                "http://maven.polopoly.com/nexus/content/groups/polopoly-plugins",
                false
        );
        doTests(conf, 46, 35);
    }

    @Ignore
    @Test
    public void testNexus2() throws Exception {
        final Configuration conf = new Configuration(
                "http://52.211.241.109/nexus",
                "ppjenkins",
                "dxthlps1",
                "com.atex.plugins&repositoryId=polopoly-plugins",
                true,
                "http://52.211.241.109/nexus/content/groups/polopoly-plugins",
                false
        );
        doTests(conf);
    }

    @Test
    public void testNexus3() throws Exception {
        final Configuration conf = new Configuration(
                "http://maven.polopoly.com/nexus",
                "ppjenkins",
                "dxthlps1",
                "com.atex.plugins&repositoryId=polopoly-plugins",
                true,
                "http://maven.polopoly.com/nexus/content/groups/polopoly-plugins",
                true
        );
        doTests(conf, 45);
    }

    private void doTests(final Configuration conf) throws Exception {
        doTests(conf, 46, 125);
    }

    private void doTests(final Configuration conf,
                         final int expectedRepositories) throws Exception {
        doTests(conf, expectedRepositories, 129);
    }

    private void doTests(final Configuration conf,
                         final int expectedRepositories,
                         final int expectedPlugins) throws Exception {
        final MetadataManager mgr = new MetadataManager(conf);
        final List<Repository> repositories = mgr.getRepositories();
        for (final Repository r : repositories) {
            System.out.println(r);
        }
        Assert.assertEquals(expectedRepositories, repositories.size());

        {
            final String searchURI = mgr.getSearchURI("com.atex.plugins", "baseline");
            System.out.println(searchURI);
            Assert.assertNotNull(searchURI);
            Assert.assertTrue(searchURI.contains("baseline"));
            Assert.assertTrue(searchURI.contains("com.atex.plugins"));
        }

        {
            final StopWatch stopWatch = StopWatch.started();
            final ExtendedModel baseline = mgr.getMetadata("com.atex.plugins&repositoryId=polopoly-plugins", "baseline", null);
            System.out.println(baseline);
            Assert.assertNotNull(baseline);
            Assert.assertEquals("4.0.13", baseline.getVersion());
            final List<Artifact> artifacts = baseline.getArtifacts();
            Assert.assertEquals(26, artifacts.size());
            System.out.println(stopWatch.stop().elapsed("baseline 4.0.13"));
        }

        {
            final StopWatch stopWatch = StopWatch.started();
            final ExtendedModel baseline = mgr.getMetadata("com.atex.plugins&repositoryId=polopoly-plugins", "baseline", "4.0.10");
            System.out.println(baseline);
            Assert.assertNotNull(baseline);
            Assert.assertEquals("4.0.10", baseline.getVersion());
            final List<Artifact> artifacts = baseline.getArtifacts();
            Assert.assertEquals(26, artifacts.size());
            System.out.println(stopWatch.stop().elapsed("baseline 4.0.10"));
        }

        /* This is really slow in nexus 3
        {
            final StopWatch stopWatch = StopWatch.started();
            // we only get the releases, so even if asking for a snapshot repository we will get a null
            final ExtendedModel baseline = mgr.getMetadata("com.atex.plugins&repositoryId=polopoly-plugins-snapshot", "baseline", null);
            Assert.assertNull(baseline);
            System.out.println(stopWatch.stop().elapsed("baseline null"));
        }
         */

        {
            final StopWatch stopWatch = StopWatch.started();
            final ExtendedModel model = mgr.getMetadata("com.atex.plugins&repositoryId=polopoly-plugins", "textmining-opencalais-top", null);
            System.out.println(model);
            Assert.assertNotNull(model);
            Assert.assertEquals("1.6", model.getVersion());
            System.out.println(stopWatch.stop().elapsed("textmining-opencalais-top"));
        }
        {
            final StopWatch stopWatch = StopWatch.started();
            final ExtendedModel model = mgr.getMetadata("com.atex.plugins&repositoryId=polopoly-plugins", "textmining-opencalais-server", null);
            System.out.println(model);
            Assert.assertNotNull(model);
            Assert.assertEquals("1.6", model.getVersion());
            System.out.println(stopWatch.stop().elapsed("textmining-opencalais-server"));
        }
        {
            final StopWatch stopWatch = StopWatch.started();
            final ExtendedModel model = mgr.getMetadata("com.atex.plugins&repositoryId=polopoly-plugins", "textmining-opencalais-plugin", null);
            System.out.println(model);
            Assert.assertNotNull(model);
            Assert.assertEquals(null, model.getVersion());
            System.out.println(stopWatch.stop().elapsed("textmining-opencalais-plugin"));
        }

        {
            final StopWatch stopWatch = StopWatch.started();
            ExtendedModel baseline = null;
            final List<ExtendedModel> metadatas = mgr.getMetadatas("com.atex.plugins&repositoryId=polopoly-plugins");
            for (final ExtendedModel model : metadatas) {
                System.out.println(model);
                if (model.getArtifactId().equals("baseline")) {
                    baseline = model;
                }
            }
            Assert.assertEquals(expectedPlugins, metadatas.size());

            Assert.assertNotNull(baseline);
            Assert.assertEquals("4.0.13", baseline.getVersion());
            final List<Artifact> artifacts = baseline.getArtifacts();
            Assert.assertEquals(1, artifacts.size());
            System.out.println(stopWatch.stop().elapsed("getMetadatas"));
        }

    }

}