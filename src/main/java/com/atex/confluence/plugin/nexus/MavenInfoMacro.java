package com.atex.confluence.plugin.nexus;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.CiManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.extras.common.org.springframework.util.StringUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;

public class MavenInfoMacro extends BaseMacro {

    private static final String RELASE_NOTE_KEY = "releaseNote";
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenInfoMacro.class);
    private static final String MAVEN_SITE_TITLE = "Link to Maven Site";

    private final SubRenderer subRenderer;
    private final MetadataManager metadataManager;
    

    public MavenInfoMacro(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate, SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
        ConfigurationReader configurationReader = new ConfigurationReader(pluginSettingsFactory);
        Configuration configuration = transactionTemplate.execute(configurationReader);
        metadataManager = new MetadataManager(configuration);
    }

    @Override
    public boolean isInline() {
        return false;
    }

    @Override
    public boolean hasBody() {
        return false;
    }

    @Override
    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }

    /**
     * This method returns XHTML to be displayed on the page that uses this
     * macro we just do random stuff here, trying to show how you can access the
     * most basic managers and model objects. No emphasis is put on beauty of
     * code nor on doing actually useful things :-)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String execute(Map params, String body, RenderContext renderContext) throws MacroException {
        StringBuilder result = new StringBuilder();
        Object keyList[] = null;
        String groupId = null;
        String artifactId = null;
        String releaseNote = null;
        if (!params.isEmpty()) {
            keyList = params.keySet().toArray();
        }

        if(keyList != null) {
            for (Object key : keyList) {
                if ("groupid".equalsIgnoreCase(key.toString())) {
                    groupId = (String) params.get(key.toString());
                }
                if ("artifactid".equalsIgnoreCase(key.toString())) {
                    artifactId = (String) params.get(key.toString());
                }
                if(RELASE_NOTE_KEY.equalsIgnoreCase(key.toString())) {
                    releaseNote = params.get(key).toString();
                }
            }
        }
        if (artifactId != null) {
            result.append(getPluginMetaDataTable(groupId, artifactId, releaseNote));
        } else {
            result.append(getPluginListTable(groupId));
        }
        return subRenderer.render(result.toString(), renderContext);
    }

    private String getPluginListTable(String groupId) {
        StringBuilder result = new StringBuilder();
        List<ExtendedModel> models;
        try {
            models = metadataManager.getMetadatas(groupId);

            result.append("h3. Plugin List \n");
            result.append("|| Name || Artifact Id || Latest Version || Description || \n");
            for (ExtendedModel model : models) {
                result.append("| [");
                result.append(getName(model));
                result.append("|");
                result.append((model.getUrl() != null) ? model.getUrl() : toValidUrlName(getName(model)));
                result.append("]");
                result.append("|");
                result.append(model.getArtifactId());
                result.append("|");
                result.append(getVersion(model));
                result.append("|");
                result.append(parseString(model.getDescription()));
                result.append(" | \n ");
            }
            result.append("\n");
            if (models.isEmpty()) {
                return "{warning}No plugins available{warning}";
            } else {
                return result.toString();
            }
        } catch (AddressNotFoundException e) {
            result.append("{warning}Please make sure the Nexus url is correctly configured{warning}");
        } catch (UnAuthorizeException e) {
            result.append("{warning}Please make sure the credential for Nexus is correctly configured{warning}");
        } catch (IOException e) {
            result.append("{warning}Error retrieving metadata{warning}");
        }
        return result.toString();
    }

    private String getPluginMetaDataTable(String groupId, String artifactId, String releaseNote) {
        ExtendedModel model;
        StringBuilder result = new StringBuilder();
        try {
            model = metadataManager.getMetadata(groupId, artifactId);
            if (model != null) {
                IssueManagement issueManagement = model.getIssueManagement();
                Scm scm = model.getScm();
                Organization org = model.getOrganization();
                List<License> licenses = model.getLicenses();
                CiManagement cim = model.getCiManagement();
                result.append(" h3. Metadata for ");
                result.append(parseString(model.getName()));
                result.append("\n || Group Id | ");
                result.append(getGroupId(model));
                result.append(" || Artifact Id | ");
                result.append(model.getArtifactId());
                result.append("| \n || Release(s) | ");
                result.append(getVersions(model));
                result.append(" || Developers | ");
                result.append(getDeveloperInfo(model.getDevelopers()));
                result.append("| \n || Source Code | ");
                result.append(getSourceCode(scm));
                result.append(" || Source Code(Read Only) | ");
                result.append(getConnection(scm));
                result.append("| \n || Organization | ");
                result.append(getOrganization(org));
                result.append(" || Issue Tracking | ");
                result.append(getIssueInfo(issueManagement));
                result.append("| \n || CI Environment | ");
                result.append(getCIEnv(cim));
                result.append(" || License | ");
                result.append(getLicenses(licenses));
                result.append(" | \n || Maven Site | ");
                result.append(getLinkToSite(model));
                result.append(" || Maven Repositories | ");
                result.append(getMavenRepo(model));
                result.append(" | \n ");
                if(releaseNote != null && !releaseNote.trim().isEmpty()) {
                    try {
                        // validate URL
                        new URL(releaseNote);
                        result.append("|| Release Note |").append(parseUrlLabel("Release Note", releaseNote)).append("| \n");
                    } catch (MalformedURLException e) {
                        result.append("|| Release Note |{warning}").append(releaseNote).append(" is not valid URL{warning}").append("| \n");
                    }
                }
                result.append(" h5. Description \n ");
                result.append(" {excerpt:hidden=true} ");
                result.append(parseString(model.getDescription()).replaceAll("\n", " "));
                result.append(" {excerpt} \n ");
                result.append(parseString(model.getDescription()));
                
            } else {
                result.append("{warning}Metadata model not available{warning}");
            }
        } catch (AddressNotFoundException e) {
            result.append("{warning}Please make sure the Nexus url is correctly configured{warning}");
        } catch (UnAuthorizeException e) {
            result.append("{warning}Please make sure the credential for Nexus is correctly configured{warning}");
        } catch (IOException e) {
            result.append("{warning}Error retrieving metadata{warning}");
        }
        return result.toString();
    }

    private String getConnection(Scm scm) {
        if(scm != null) {
            String connection = scm.getConnection();
            if(connection != null && !connection.trim().isEmpty()) {
                if(StringUtils.countOccurrencesOf(connection, ":") > 2) {
                    int index = connection.indexOf(":");
                    connection = connection.substring(index + 1);
                    index = connection.indexOf(":");
                    connection = connection.substring(index + 1);
                    if(!connection.startsWith("http")) {
                        return connection;
                    }
                }
            }
            return parseUrlLabel("Read Only", connection);            
        } else {
            return "";
        }
    }

    private String getCIEnv(CiManagement cim) {
        StringBuilder result = new StringBuilder();
        if (cim !=null) {
            result.append(parseUrlLabel(cim.getSystem(), cim.getUrl()));
        }
        return result.toString();
    }

    private String getVersions(ExtendedModel model) {
        StringBuilder builder = new StringBuilder();
        for(Artifact a: model.getArtifacts()) {
            if(builder.length() != 0) {
                builder.append("\n");
            }
            builder.append(a.getVersion());
        }
        
        return builder.toString();
    }
    private String getSourceCode(Scm scm) {
        StringBuilder result = new StringBuilder();
        if (scm !=null) {
            result.append(parseString(scm.getUrl()));
        }
        return result.toString();
    }

    private String getOrganization(Organization org) {
        StringBuilder result = new StringBuilder();
        if (org !=null) {
            result.append(parseUrlLabel(org.getName(), org.getUrl()));
        }
        return result.toString();
    }
    
    private String getLinkToSite(Model model) {
        DistributionManagement distribution = model.getDistributionManagement();
        if(distribution != null) {
            Site site = distribution.getSite();
            if(site != null && site.getUrl() != null) {
                return parseUrlLabel(MAVEN_SITE_TITLE, site.getUrl());
            }
        }
        Configuration configuration = metadataManager.getConfiguration();
        String url = "";
        if(configuration != null && configuration.isGenerateLink()) {
            // no url specified
            // construct one
            // url format will be according to format https://github.com/polopoly/nexus-jar-reader-plugin
            String artifactId = model.getArtifactId();
            String baseUrl = getNexusUrl(model);
            if(baseUrl == null || baseUrl.trim().isEmpty()) {
                return "";
            }
            url = baseUrl + artifactId + "-" + getVersion(model) + "-site.jar" + "!/index.html" ;            
        }
        return parseUrlLabel(MAVEN_SITE_TITLE, url);
    }

    private String getMavenRepo(Model model) {
        String url = getNexusUrl(model) ;
        if(url != null && !url.trim().isEmpty()) {
            return parseUrlLabel("Link to Maven Repo", url);
        } else {
            return "";
        }
    }
    
    private String getNexusUrl(Model model) {
        DistributionManagement distribution = model.getDistributionManagement();
        if(distribution == null) {
            LOGGER.debug("Distribution Management for " + model.toString() + " is not define in pom file.");
            return null;
        }
        DeploymentRepository repository = distribution.getRepository();
        
        if(repository == null) {
            LOGGER.debug("Deployment Repository for " + model.toString() + " is not define in pom file.");
            return null;
        }
        String groupId = getGroupId(model).replace(".", "/");
        String artifactId = model.getArtifactId().replace(".", "/");
        String url = repository.getUrl();
        if(url == null) {
            LOGGER.debug("Deployment Repository's URL for " + model.toString() + " is not define in pom file.");
            return null;
        }
        if(!url.endsWith("/")) {
            url = url + "/";
        }
        url = url + groupId + "/" + artifactId + "/" + getVersion(model) + "/" ;
        return url;
    }

    private String getLicenses(List<License> licenses) {
        StringBuilder result = new StringBuilder();
        for (int licCount = 0; licCount < licenses.size(); licCount++) {
            License lic = licenses.get(licCount);
            if (licCount != 0) {
                result.append("\n");
            }
            result.append(parseUrlLabel(lic.getName(), lic.getUrl()));
        }
        return result.toString();
    }

    private String getIssueInfo(IssueManagement issMan) {
        StringBuilder result = new StringBuilder();
        if (issMan != null) {
            result.append(parseUrlLabel(issMan.getSystem(), issMan.getUrl()));
        }
        return result.toString();
    }

    private String getDeveloperInfo(List<Developer> developers) {
        StringBuilder result = new StringBuilder();
        for (int devCount = 0; devCount < developers.size(); devCount++) {
            Developer dev = developers.get(devCount);
            if (devCount != 0) {
                result.append("\n");
            }
            String devEmail = dev.getEmail();
            if (devEmail != null) {
                result.append("[");
                result.append(dev.getName());
                result.append("| mailto:");
                result.append(devEmail);
                result.append("]");
            } else {
                result.append(dev.getName());
            }
        }
        return result.toString();
    }

    private String getGroupId(Model model) {
        String result = "";
        String temp = model.getGroupId();
        Parent parent = model.getParent();
        if (temp != null) {
            result = model.getGroupId();
        } else {
            if (parent != null) {
                result = parent.getGroupId();
            }
        }
        return result;
    }

    private String getVersion(Model model) {
        String result = "";
        String temp = model.getVersion();
        Parent parent = model.getParent();
        if (temp != null) {
            result = model.getVersion();
        } else {
            if (parent != null) {
                result = parent.getVersion();
            }
        }
        return result;
    }

    private String parseString(String field) {
        if (field == null) {
            return "";
        }
        return field;
    }
    
    private String parseUrlLabel(String rawLabel, String rawUrl) {
        StringBuilder result = new StringBuilder();
        String label = parseString(rawLabel);
        String url = parseString(rawUrl);               
        if (label.length() > 0 && url.length() > 0) {
            result.append("[");
            result.append(label);
            result.append("|");
            result.append(url);
            result.append("]");
        } else if (label.length() > 0 && url.length() == 0) {
            result.append(label);
        } else if (label.length() == 0 && url.length() > 0) {
            result.append(url);
        }
        return result.toString();
    }

    /**
     * This method return the displayable name of the model in order of model
     * name model groupId "Not Available" string
     *
     * @param model
     * @return the display name of the model
     */
    private String getName(Model model) {
        String name = parseString(model.getName()).trim();
        if ("".equals(name)) {
            name = getGroupId(model);
            if ("".equals(name)) {
                name = "Not Available";
            }
        }
        return name;
    }

    private String toValidUrlName(String field) {
        String invalidPattern = "/";
        String name = parseString(field);
        return name.replace(invalidPattern, "");
    }
}
