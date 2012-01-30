package com.atex.confluence.plugin.nexus;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.maven.model.*;

public class MavenInfoMacro extends BaseMacro {

    // We just have to define the variables and the setters, then Spring injects the correct objects for us to use. Simple and efficient.
    // You just need to know *what* you want to inject and use.
    private final SubRenderer subRenderer;
    private final MetadataManager metadataManager;
    private final static String NO_INFO = "";
    

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
        StringBuffer result = new StringBuffer();
        Object keyList[] = null;
        String groupId = null;
        String artifactId = null;
        if (!params.isEmpty()) {
            keyList = params.keySet().toArray();
        }

        for (Object key : keyList) {
            if ("groupid".equalsIgnoreCase(key.toString())) {
                groupId = (String) params.get(key.toString());
            }
            if ("artifactid".equalsIgnoreCase(key.toString())) {
                artifactId = (String) params.get(key.toString());
            }
        }
        if (artifactId != null) {
            result.append(getPluginMetaDataTable(groupId, artifactId));
        } else {
            result.append(getPluginListTable(groupId));
        }
        return subRenderer.render(result.toString(), renderContext);
    }

    private String getPluginListTable(String groupId) {
        StringBuffer result = new StringBuffer();
        List<Model> models;
        try {
            models = metadataManager.getMetadatas(groupId);

            result.append("h3. Plugin List \n");
            result.append("|| Name || Artifact Id || Version || Description || \n");
            for (Model model : models) {
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

    // Date: Jan 10, lee
    // 1. set excerpt hidden:true
    // 2. replace new line with empty String 
    // 3. remove comment
    private String getPluginMetaDataTable(String groupId, String artifactId) {
        Model model;
        StringBuffer result = new StringBuffer();
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
                result.append("| \n || Latest Release | ");
                result.append(getVersion(model));
                result.append(" || Source Code | ");
                result.append((scm != null) ? parseString(scm.getUrl()) : "");
                result.append("| \n || Developers | ");
                result.append(getDeveloperInfo(model.getDevelopers()));
                result.append(" || Issue Tracking | ");
                result.append(getIssueInfo(issueManagement));
                result.append("| \n || Organization | ");
                result.append(getOrganization(org));
                result.append(" || License | ");
                result.append(getLicenses(licenses));
                result.append("| \n || Source Code | ");
                result.append(getSourceCode(model));
                result.append(" || Maven Repo | ");
                result.append(getMavenRepo(groupId, artifactId));
                result.append("| \n || CI Environment | ");
                result.append(getCIEnv(cim));
                result.append(" | \n ");
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

    private Object getCIEnv(CiManagement cim) {
        // TODO Auto-generated method stub
        return null;
    }

    private Object getSourceCode(Model model) {
        // TODO Auto-generated method stub
        return null;
    }

    private Object getOrganization(Organization org) {
        String result = NO_INFO;
        if (org !=null) {
            if (org.getUrl() !=null) {
                result = org.getUrl();
            }
        }
        return result;
    }

    private Object getMavenRepo(String groupId, String artifactId) {
        // TODO Auto-generated method stub
        return null;
    }

    private Object getLicenses(List<License> licenses) {
        StringBuffer result = new StringBuffer();
        for (int licCount = 0; licCount < licenses.size(); licCount++) {
            License lic = licenses.get(licCount);
            if (licCount != 0) {
                result.append("\n");
            }
            String licUrl = lic.getUrl();
            if (licUrl != null) {
                result.append(licUrl);
            } else if (lic.getName() !=null) {
                result.append(lic.getName());
            } else {
                result.append(NO_INFO);
            }

        }
        return result.toString();
    }

    private String getIssueInfo(IssueManagement issueManagement) {
        StringBuffer result = new StringBuffer();
        if (issueManagement != null) {
            if (issueManagement.getUrl() != null) {
                result.append("[");
                result.append(parseString(issueManagement.getSystem()));
                result.append("|");
                result.append(parseString(issueManagement.getUrl()));
                result.append("]");
            } else {
                result.append(parseString(issueManagement.getSystem()));
            }
        }
        return result.toString();
    }

    private String getDeveloperInfo(List<Developer> developers) {
        StringBuffer result = new StringBuffer();
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
