package com.atex.confluence.plugin.nexus.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.atex.confluence.plugin.nexus.config.Configuration;
import com.atex.confluence.plugin.nexus.connect.Repository;
import com.atex.confluence.plugin.nexus.connect.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Nexus3MetaManager
 *
 * @author mnova
 */
public class Nexus3MetaManager extends AbstractMetadataManager implements NexusMetadataManager {

    private static final Logger LOGGER = Logger.getLogger(Nexus3MetaManager.class.getName());

    private static final String MAVEN_FORMAT = "maven2";
    private static final String REPOSITORY_ID_PARAM = "&repositoryId=";

    public Nexus3MetaManager(final Configuration configuration) {
        super(configuration);
    }

    @Override
    public List<Repository> getRepositories() throws IOException, ParserConfigurationException, SAXException {
        final HttpMethod get = doGetHttpMethod(getConfiguration().getSearchRepositoriesURI());
        return parseRepositories(get.getResponseBodyAsString());
    }

    @Override
    public String getSearchURI(final String groupId, final String artifactId) {
        final Configuration configuration = getConfiguration();

        final StringBuilder searchURI = new StringBuilder(configuration.getSearchURI());
        searchURI.append("?group=");
        final String gid;
        if (StringUtils.isBlank(groupId)) {
            gid = nullToEmpty(configuration.getGroupId()).trim();
        } else {
            gid = groupId.trim();
        }
        final String realGroupId;
        final String repo;
        final int idx = gid.indexOf(REPOSITORY_ID_PARAM);
        if (idx > 0) {
            realGroupId = gid.substring(0, idx);
            repo = gid.substring(idx + REPOSITORY_ID_PARAM.length());
        } else {
            realGroupId = gid;
            repo = null;
        }
        searchURI.append(realGroupId);
        if (!StringUtils.isBlank(repo)) {
            searchURI.append("&repository=");
            searchURI.append(repo.trim());
        }
        if (!StringUtils.isBlank(artifactId)) {
            searchURI.append("&name=");
            searchURI.append(artifactId.trim());
        }
        return searchURI.toString();
    }

    @Override
    public Response getResponse(final String groupId,
                                final String artifactId) throws IOException {
        LOGGER.info(String.format("get %s:%s", groupId, artifactId));
        final String searchURI = getSearchURI(groupId, artifactId)
                + "&format=maven2&maven.extension=pom";
        final Response response = new Response();
        final Map<String, String> latestReleases = new HashMap<String, String>();
        final Map<String, String> latestSnapshots = new HashMap<String, String>();
        final Map<String, String> repoReleases = new HashMap<String, String>();
        final Map<String, String> repoSnapshots = new HashMap<String, String>();
        final Map<String, List<Artifact>> artifacts = new HashMap<String, List<Artifact>>();
        final Set<String> artifactsSet = new HashSet<String>();
        String uri = searchURI;
        do {
            LOGGER.info(String.format("fetch %s", uri));
            final HttpMethod get = doGetHttpMethod(uri);
            final String json = get.getResponseBodyAsString();
            final ArtifactsResult results = parseArtifacts(json);
            for (final Artifact a : results.getArtifacts()) {
                if (StringUtils.isNotBlank(a.getClassifier())) {
                    continue;
                }
                if (!a.getType().equals("pom")) {
                    continue;
                }
                final String key = String.format(
                        "%s:%s:%s:%s",
                        a.getGroupId(),
                        a.getArtifactId(),
                        a.getClassifier(),
                        a.getType()
                );

                // make sure we only add unique artifacts.
                {
                    final String setKey = key + ":" + a.getVersion();
                    if (artifactsSet.contains(setKey)) {
                        continue;
                    }
                    artifactsSet.add(setKey);
                }

                final boolean isSnapshot = a.getUrl().contains("-SNAPSHOT/");
                final String version = a.getVersion();
                if (isSnapshot) {
                    if (setLastVersion(latestSnapshots, key, version)) {
                        repoSnapshots.put(key, a.getRepositoryId());
                    }
                } else {
                    if (setLastVersion(latestReleases, key, version)) {
                        repoReleases.put(key, a.getRepositoryId());
                    }
                }
                List<Artifact> list = artifacts.get(key);
                if (list == null) {
                    list = new ArrayList<Artifact>();
                    artifacts.put(key, list);
                }
                list.add(a);
                response.addArtifact(a);
            }
            if (results.getToken() != null) {
                uri = searchURI + "&continuationToken=" + results.getToken();
            } else {
                break;
            }
        } while (true);

        // now try to set the latest versions for all the artifacts

        for (final String key : latestReleases.keySet()) {
            final String version = latestReleases.get(key);
            //System.out.println(key + " -> " + version);
            final List<Artifact> list = artifacts.get(key);
            if (list != null) {
                for (final Artifact a : list) {
                    a.setLatestRelease(version);
                    a.setLatestReleaseRepositoryId(repoReleases.get(key));
                }
            }
        }
        for (final String key : latestSnapshots.keySet()) {
            final String version = latestSnapshots.get(key);
            //System.out.println(key + " -> " + version);
            final List<Artifact> list = artifacts.get(key);
            if (list != null) {
                for (final Artifact a : list) {
                    a.setLatestSnapshot(version);
                    a.setLatestSnapshotRepositoryId(repoSnapshots.get(key));
                }
            }
        }

        // and finally add the repositories
        try {
            response.getRepositories().addAll(getRepositories());
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }
        return response;
    }

    private boolean setLastVersion(final Map<String, String> versions,
                                   final String key,
                                   final String version) {
        final String lastVersion = versions.get(key);
        if (lastVersion == null) {
            versions.put(key, version);
            return true;
        } else {
            final ComparableVersion lv = new ComparableVersion(lastVersion);
            final ComparableVersion cv = new ComparableVersion(version);
            if (cv.compareTo(lv) > 0) {
                versions.put(key, version);
                return true;
            }
        }
        return false;
    }

    private ArtifactsResult parseArtifacts(final String body) {
        final List<Artifact> artifacts = new ArrayList<Artifact>();
        final JsonElement element = new JsonParser().parse(body);
        final JsonObject json = element.getAsJsonObject();
        if (json.has("items")) {
            final JsonArray items = json.get("items").getAsJsonArray();
            for (int idx = 0; idx < items.size(); idx++) {
                final JsonObject artifact = items.get(idx).getAsJsonObject();
                final Artifact a = from(artifact);
                if (a != null) {
                    artifacts.add(a);
                }
            }
        }
        if (json.has("continuationToken")) {
            return new ArtifactsResult(artifacts, getJsonString(json, "continuationToken"));
        }
        return new ArtifactsResult(artifacts);
    }

    private Artifact from(final JsonObject json) {
        final String format = getJsonString(json, "format");
        if (MAVEN_FORMAT.equals(format)) {
            final JsonObject maven = json.getAsJsonObject("maven2");
            final String extension = getJsonString(maven, "extension");
            if (Arrays.asList("jar", "pom", "war").contains(extension)) {
                final Artifact artifact = new Artifact();
                artifact.setGroupId(getJsonString(maven, "groupId"));
                artifact.setArtifactId(getJsonString(maven, "artifactId"));
                artifact.setVersion(getJsonString(maven, "version"));
                artifact.setClassifier(nullToEmpty(getJsonString(maven, "classifier")));
                artifact.setType(extension);
                artifact.setRepositoryId(getJsonString(json, "repository"));
                artifact.setUrl(getJsonString(json, "downloadUrl"));
                return artifact;
            }
        }
        return null;
    }

    private List<Repository> parseRepositories(final String body) {
        final List<Repository> repositories = new ArrayList<Repository>();
        final JsonElement json = new JsonParser().parse(body);
        if (json.isJsonArray()) {
            final JsonArray array = json.getAsJsonArray();
            for (int idx = 0; idx < array.size(); idx++) {
                final JsonObject obj = array.get(idx).getAsJsonObject();
                final String format = getJsonString(obj, "format");
                if (MAVEN_FORMAT.equals(format)) {
                    final Repository repo = new Repository();
                    repo.setRepositoryId(getJsonString(obj, "name"));
                    repo.setRepositoryURL(getJsonString(obj, "url"));
                    repositories.add(repo);
                }
            }
        }
        return repositories;
    }

    private String nullToEmpty(final String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    private String getJsonString(final JsonObject obj,
                                 final String name) {
        if (obj != null && obj.has(name)) {
            final JsonElement jsonElement = obj.get(name);
            if (jsonElement.isJsonNull()) {
                return null;
            }
            if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsString();
            }
        }
        return null;
    }

    private static class ArtifactsResult {
        private final List<Artifact> artifacts;
        private final String token;

        public ArtifactsResult(final List<Artifact> artifacts) {
            this.artifacts = artifacts;
            this.token = null;
        }

        public ArtifactsResult(final List<Artifact> artifacts, final String token) {
            this.artifacts = artifacts;
            this.token = token;
        }

        public List<Artifact> getArtifacts() {
            return artifacts;
        }

        public String getToken() {
            return token;
        }
    }
}
