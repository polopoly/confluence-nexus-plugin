package com.atex.confluence.plugin.nexus;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author pau
 *
 */
public class MetadataManager {
    
    private static final Configuration CONFIGURATION = new Configuration();
    
    public MetadataManager() { }
    
    /**
     * Search maven model based on groupId
     * @param groupId of the maven model to be search
     * @return list of null safe model
     * @throws IOException when search failed
     */
    public List<Model> getMetadatas(String groupId) throws IOException {
        Response result = getResponse(groupId);
        return getPoms(result.getLatestReleases(), result.getRepositories());
    }
    
    /**
     * Search maven model based on groupId
     * @param artifactId of the maven to be search
     * @return model of the maven, else null when not found
     * @throws IOException when search failed
     */
    public Model getMetadata(String artifactId) throws IOException {
        return getMetadata(null, artifactId);
    }
    
    /**
     * Search maven model based on groupId
     * @param groupId of the artifact to be search
     * @param artifactId of the maven to be search
     * @return model of the maven, else null when not found
     * @throws IOException when search failed
     */
    public Model getMetadata(String groupId, String artifactId) throws IOException {
        Response result = getResponse(groupId);
        Artifact artifact = result.getByArtifactId(artifactId);
        if(artifact == null) {
            return null;
        }
        List<Model> models = getPoms(Arrays.asList(artifact), result.getRepositories());
        if(models != null && models.size() == 1) {
            return models.get(0);
        } else {
            return null;
        }
    }
    
    public List<Repository> getRepositories() throws IOException, ParserConfigurationException, SAXException {
        List<Repository> repositories = new ArrayList<Repository>();
        HttpMethod get = doGetHttpMethod(CONFIGURATION.getSearchRepositoriesURI());
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = builderFactory.newDocumentBuilder();
        Document dom;
        dom = db.parse(get.getResponseBodyAsStream());
        Element elemnt = dom.getDocumentElement();
        NodeList repositoriesNodes = elemnt.getElementsByTagName("repositories-item");
        if(repositoriesNodes != null && repositoriesNodes.getLength() > 0) {
            for(int i = 0 ; i < repositoriesNodes.getLength();i++) {
                Element el = (Element)repositoriesNodes.item(i);
                repositories.add(getRepository(el));
            }
        }
        return repositories;
    }
    
    private synchronized Response getResponse(String groupId) throws IOException {
        if(groupId == null || groupId.trim().isEmpty()) {
            groupId = CONFIGURATION.getGroupId();
        }
        
        HttpMethod get = doGetHttpMethod(CONFIGURATION.getSearchURI() + "?g=" + groupId);
        return parseInputStreamToResponse(get.getResponseBodyAsStream());
    }
    
    private Response parseInputStreamToResponse(InputStream inputStream) {
        Response response = new Response();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = builderFactory.newDocumentBuilder();
            Document dom;
            dom = db.parse(inputStream);
            Element elemnt = dom.getDocumentElement();
            NodeList artifactNodes = elemnt.getElementsByTagName("artifact");
            if(artifactNodes != null && artifactNodes.getLength() > 0) {
                for(int i = 0 ; i < artifactNodes.getLength();i++) {
                    Element el = (Element)artifactNodes.item(i);
                    response.addArtifact(getArtifact(el));
                }
            }
            response.getRepositories().addAll(getRepositories());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return response;
    }
    
    private Repository getRepository(Element el) {
        Repository repository = new Repository();
        repository.setRepositoryId(getTextValue(el, "id"));
        repository.setRepositoryURL(getTextValue(el, "contentResourceURI"));
        
        return repository;
    }
    
    private Artifact getArtifact(Element el) {
        Artifact artifact = new Artifact();
        artifact.setGroupId(getTextValue(el, "groupId"));
        artifact.setArtifactId(getTextValue(el, "artifactId"));
        artifact.setVersion(getTextValue(el, "version"));
        artifact.setLatestSnapshot(getTextValue(el, "latestSnapshot"));
        artifact.setLatestSnapshotRepositoryId(getTextValue(el, "latestSnapshotRepositoryId"));
        artifact.setLatestRelease(getTextValue(el, "latestRelease"));
        artifact.setLatestRelease(getTextValue(el, "latestRelease"));
        artifact.setLatestReleaseRepositoryId(getTextValue(el, "latestReleaseRepositoryId"));
        
        return artifact;
    }
    
    private String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
        }
        return textVal;
    }
    
    private List<Model> getPoms(List<Artifact> artifacts, List<Repository> repositories) throws IOException {
        List<Model> poms = new ArrayList<Model>();
        for(Artifact artifact: artifacts) {
            if(artifact == null) {
                continue;
            }
            HttpMethod get = doGetHttpMethod(getUrl(artifact, repositories));
            MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
            try {
                Model model = mavenXpp3Reader.read(get.getResponseBodyAsStream());
                poms.add(model);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return poms;
    }
    
    private HttpMethod doGetHttpMethod(String url) throws HttpException, IOException {
        HttpMethod get = new GetMethod(url);
        HttpClient client = new HttpClient();
        client.getState().setCredentials(CONFIGURATION.getAuthScope(), CONFIGURATION.getCredentials());
        int status = client.executeMethod(get);
        if(status != HttpStatus.SC_OK) {
            String message = "Failed to request url " + url + ", returned status: " + status;
            if(status == HttpStatus.SC_UNAUTHORIZED) {
                message += ", Reason: Unauthorized";
            }
            throw new IOException(message);
        }
        return get;
    }
    
    private String getUrl(Artifact artifact, List<Repository> repositories) {
        String groupIdPath = artifact.getGroupId().replace(".", "/");
        String url = "/" + groupIdPath + "/" + artifact.getArtifactId() + "/" + artifact.getVersion() + "/" + artifact.getArtifactId() + "-" + artifact.getVersion() + ".pom";
        for(Repository repository: repositories) {
            if(repository.getRepositoryId().equals(artifact.getLatestReleaseRepositoryId())) {
                url = repository.getRepositoryURL() + url;
                break;
            }
        }
        return url;
    }
}
