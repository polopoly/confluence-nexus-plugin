package com.atex.confluence.plugin.nexus.data;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.maven.model.Model;
import org.xml.sax.SAXException;

import com.atex.confluence.plugin.nexus.connect.Repository;
import com.atex.confluence.plugin.nexus.connect.Response;
import com.atex.confluence.plugin.nexus.connect.UnAuthorizeException;

/**
 * NexusMetadataManager
 *
 * @author mnova
 */
public interface NexusMetadataManager {

    List<Repository> getRepositories() throws IOException, ParserConfigurationException, SAXException;

    String getSearchURI(String groupId, String artifactId);

    Response getResponse(String groupId, String artifactId) throws IOException;

    Model getPom(final String url) throws IOException;
}
