package com.atex.confluence.plugin.nexus.data;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atex.confluence.plugin.nexus.config.Configuration;
import com.atex.confluence.plugin.nexus.connect.AddressNotFoundException;
import com.atex.confluence.plugin.nexus.connect.UnAuthorizeException;
import com.atex.confluence.plugin.nexus.util.StopWatch;
import com.atlassian.user.util.Base64Encoder;

/**
 * AbstractMetadataManager
 *
 * @author mnova
 */
public abstract class AbstractMetadataManager implements NexusMetadataManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMetadataManager.class);

    private final Configuration configuration;

    public AbstractMetadataManager(final Configuration configuration) {
        this.configuration = configuration;
    }

    protected Configuration getConfiguration() {
        return configuration;
    }

    protected HttpMethod doGetHttpMethod(final String url) throws IOException {
        final StopWatch stopWatch = StopWatch.started();
        try {
            final HttpMethod get = withCredentials(new GetMethod(url));
            final HttpClient client = new HttpClient();
            int status = client.executeMethod(get);
            if (status != HttpStatus.SC_OK) {
                String message = "Failed to request url " + url + ", returned status: " + status;
                if (status == HttpStatus.SC_UNAUTHORIZED) {
                    LOGGER.error(message);
                    throw new UnAuthorizeException(message);
                } else if (status == HttpStatus.SC_NOT_FOUND) {
                    LOGGER.error(message);
                    throw new AddressNotFoundException(message);
                }
                LOGGER.error(message);
                throw new IOException(message);
            }
            return get;
        } finally {
            LOGGER.debug(stopWatch.stop().elapsed("Get " + url));
        }
    }

    @Override
    public Model getPom(final String url) throws IOException {
        try {
            final HttpMethod get = doGetHttpMethod(url);
            final MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
            return mavenXpp3Reader.read(get.getResponseBodyAsStream());
        } catch (IllegalStateException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (XmlPullParserException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (AddressNotFoundException e) {
            LOGGER.warn(e.getMessage(), e);
        } catch (UnAuthorizeException e) {
            throw new UnAuthorizeException(e);
        } catch (IOException e) {
            LOGGER.warn(e.getMessage(), e);
            throw e;
        }
        return null;
    }

    /**
     * This method was used for nexus 1 but, for some reasons, fails to work with the nexus3 test instance.
     *
     * @param client a not null client.
     * @return the provided client.
     */
    protected HttpClient withCredentials(final HttpClient client) {
        client.getState().setCredentials(configuration.getAuthScope(), configuration.getCredentials());
        return client;
    }

    /**
     * This method works across nexus 1, 2 and 3.
     *
     * @param method
     * @return
     */
    protected HttpMethod withCredentials(final HttpMethod method) {
        method.addRequestHeader("Authorization", "Basic " + encodeUser());
        return method;
    }

    private String encodeUser() {
        final String value = configuration.getUsername() + ":" + configuration.getPassword();
        final byte[] result = Base64Encoder.encode(value.getBytes());
        return new String(result);
    }

}
