package com.atex.confluence.plugin.nexus.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.atex.confluence.plugin.nexus.config.Configuration;
import com.atex.confluence.plugin.nexus.connect.Repository;
import com.atex.confluence.plugin.nexus.connect.Response;

/**
 * @author pau
 */
public class MetadataManager {

    // use static variable to cache the configuration across all class instances
    private static Configuration configuration;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataManager.class);

    private final NexusMetadataManager nexusMgr;

    public MetadataManager(final Configuration configuration) {
        setConfiguration(configuration);
        if (configuration.isNexus3()) {
            nexusMgr = new Nexus3MetaManager(configuration);
        } else {
            nexusMgr = new NexusMetaManager(configuration);
        }
    }

    /**
     * Search maven model based on groupId
     *
     * @param groupId of the maven model to be search
     * @return list of null safe model
     * @throws IOException when search failed
     */
    public List<ExtendedModel> getMetadatas(String groupId) throws IOException {
        Response result = getResponse(groupId, null);
        return getPoms(result.getLatestReleases(), result.getRepositories());
    }

    /**
     * Search maven model based on groupId and artifactId
     *
     * @param groupId    of the artifacts to be search
     * @param artifactId of the maven to be search
     * @param version    of the version to be search, null value will get the latest version
     * @return model of the maven, else null when not found
     * @throws IOException when search failed
     */
    public ExtendedModel getMetadata(final String groupId,
                                     final String artifactId,
                                     final String version) throws IOException {
        final Response result = getResponse(groupId, artifactId);
        final Artifact artifact = result.getByArtifactId(artifactId);
        if (artifact == null) {
            return null;
        }
        final List<ExtendedModel> models = getPoms(
                Collections.singletonList(artifact),
                result.getRepositories(),
                version
        );
        if (models.size() == 1) {
            final ExtendedModel model = models.get(0);
            model.setArtifacts(result.getListByArtifactId(artifactId));
            return model;
        } else {
            return null;
        }
    }

    public List<Repository> getRepositories() throws IOException, ParserConfigurationException, SAXException {
        return nexusMgr.getRepositories();
    }

    private synchronized Response getResponse(final String groupId,
                                              final String artifactId) throws IOException {
        return nexusMgr.getResponse(groupId, artifactId);
    }

    public synchronized String getSearchURI(final String groupId, final String artifactId) {
        return nexusMgr.getSearchURI(groupId, artifactId);
    }

    private List<ExtendedModel> getPoms(List<Artifact> artifacts, List<Repository> repositories) throws IOException {
        List<ExtendedModel> poms = new ArrayList<ExtendedModel>();
        for (Artifact artifact : artifacts) {
            if (artifact == null) {
                continue;
            }
            final Model model = nexusMgr.getPom(getUrl(artifact, repositories, null));
            if (model != null) {
                poms.add(new ExtendedModel(model, artifacts));
            }
        }
        return poms;
    }

    private List<ExtendedModel> getPoms(List<Artifact> artifacts, List<Repository> repositories, String version)
            throws IOException {
        List<ExtendedModel> poms = new ArrayList<ExtendedModel>();
        for (Artifact artifact : artifacts) {
            if (artifact == null) {
                continue;
            } else {
                if (version == null || version.isEmpty()) {
                    version = artifact.getLatestRelease();
                }
            }
            final Model model = nexusMgr.getPom(getUrl(artifact, repositories, version));
            if (model != null) {
                poms.add(new ExtendedModel(model, artifacts));
            }
        }
        return poms;
    }

    /**
     * This method get the url of the specific version/release of the plugin.
     * Url of latest version will be return if version is null or empty.
     *
     * @param artifact
     * @param repositories
     * @param version
     * @return The url of the plugin by specific version.
     */
    private String getUrl(Artifact artifact, List<Repository> repositories, String version) {
        String groupIdPath = artifact.getGroupId().replace(".", "/");
        if (version == null || version.isEmpty()) {
            version = artifact.getVersion();
        }
        String url = "/" + groupIdPath + "/" + artifact.getArtifactId() + "/" + version + "/" + artifact.getArtifactId() + "-" + version + ".pom";
        for (Repository repository : repositories) {
            if (repository.getRepositoryId().equals(artifact.getLatestReleaseRepositoryId())) {
                url = repository.getRepositoryURL() + url;
                break;
            }
        }
        return url;
    }

    public static void setConfiguration(Configuration configuration) {
        MetadataManager.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return MetadataManager.configuration;
    }
}
