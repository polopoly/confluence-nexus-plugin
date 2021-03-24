package com.atex.confluence.plugin.nexus.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.atex.confluence.plugin.nexus.config.Configuration;
import com.atex.confluence.plugin.nexus.connect.Repository;
import com.atex.confluence.plugin.nexus.connect.Response;

/**
 * NexusMetaManager
 *
 * @author mnova
 */
public class NexusMetaManager extends AbstractMetadataManager implements NexusMetadataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(NexusMetaManager.class);

    public NexusMetaManager(final Configuration configuration) {
        super(configuration);
    }

    @Override
    public List<Repository> getRepositories() throws IOException, ParserConfigurationException, SAXException {
        List<Repository> repositories = new ArrayList<Repository>();
        HttpMethod get = doGetHttpMethod(getConfiguration().getSearchRepositoriesURI());
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

    @Override
    public String getSearchURI(final String groupId, final String artifactId) {
        final Configuration configuration = getConfiguration();

        final StringBuilder searchURI = new StringBuilder(configuration.getSearchURI());
        searchURI.append("?g=");
        if (StringUtils.isBlank(groupId)) {
            searchURI.append(configuration.getGroupId());
        } else {
            searchURI.append(groupId.trim());
        }
        if (!StringUtils.isBlank(artifactId)) {
            searchURI.append("&a=");
            searchURI.append(artifactId.trim());
        }
        return searchURI.toString();
    }

    @Override
    public Response getResponse(final String groupId, final String artifactId) throws IOException {
        HttpMethod get = doGetHttpMethod(getSearchURI(groupId, artifactId));
        return parseInputStreamToResponse(get.getResponseBodyAsStream());
    }

    private Response parseInputStreamToResponse(InputStream inputStream) {
        Response response = new Response();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = builderFactory.newDocumentBuilder();
            Document dom;
            dom = db.parse(inputStream);
            Element element = dom.getDocumentElement();
            NodeList artifactNodes = element.getElementsByTagName("artifact");
            if(artifactNodes != null && artifactNodes.getLength() > 0) {
                for(int i = 0 ; i < artifactNodes.getLength();i++) {
                    Element el = (Element)artifactNodes.item(i);
                    response.addArtifact(getArtifact(el));
                }
            }
            response.getRepositories().addAll(getRepositories());
        } catch (SAXException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (ParserConfigurationException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return response;
    }

    private Artifact getArtifact(Element el) {
        Artifact artifact = new Artifact();
        artifact.setGroupId(getTextValue(el, "groupId"));
        artifact.setArtifactId(getTextValue(el, "artifactId"));
        artifact.setVersion(getTextValue(el, "version"));
        artifact.setLatestSnapshot(getTextValue(el, "latestSnapshot"));
        artifact.setLatestSnapshotRepositoryId(getTextValue(el, "latestSnapshotRepositoryId"));
        artifact.setLatestRelease(getTextValue(el, "latestRelease"));
        artifact.setLatestReleaseRepositoryId(getTextValue(el, "latestReleaseRepositoryId"));

        return artifact;
    }

    private Repository getRepository(Element el) {
        Repository repository = new Repository();
        repository.setRepositoryId(getTextValue(el, "id"));
        repository.setRepositoryURL(getTextValue(el, "contentResourceURI"));

        return repository;
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

}
