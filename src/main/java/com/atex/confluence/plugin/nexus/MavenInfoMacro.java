package com.atex.confluence.plugin.nexus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Developer;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Scm;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;

public class MavenInfoMacro extends BaseMacro {
    
    // We just have to define the variables and the setters, then Spring injects the correct objects for us to use. Simple and efficient.
    // You just need to know *what* you want to inject and use.
    
    private final SubRenderer subRenderer;
    
    public MavenInfoMacro(SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
    }
    
    public boolean isInline() {
        return false;
    }
    
    public boolean hasBody() {
        return false;
    }
    
    public RenderMode getBodyRenderMode() {
        return RenderMode.ALL;
    }
    
    /**
     * This method returns XHTML to be displayed on the page that uses this macro
     * we just do random stuff here, trying to show how you can access the most basic
     * managers and model objects. No emphasis is put on beauty of code nor on
     * doing actually useful things :-)
     */
    @SuppressWarnings("rawtypes")
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
        try {
            if (artifactId!=null) {
                result.append(getPluginMetaDataTable(groupId, artifactId));
            } else {
                result.append(getPluginListTable(groupId));
            }            
        } catch (IOException e) {
            throw new MacroException(e);
        }
        return subRenderer.render(result.toString(), renderContext);
    }
    
    private String getPluginListTable(String groupId) throws IOException {
        List<Model> models = getPluginList(groupId);
        StringBuffer result = new StringBuffer();
        
        result.append("h3. Plugin List \n");
        result.append("|| Name || Artifact Id || Version || Description || \n");
        for (Model model : models) {
            result.append("| [");
            result.append(getName(model));
            result.append("|");
            result.append((model.getUrl()!=null)?model.getUrl(): toValidUrlName(getName(model)));
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
        if (models.size()==0){
            return "{warning}No plugins available{warning}";
        } else {
            return result.toString();
        }
    }
    
    // Date: Jan 10, lee
    // 1. set excerpt hidden:true
    // 2. replace new line with empty String 
    // 3. remove comment
    private String getPluginMetaDataTable(String groupId, String artifactId) throws IOException {
        Model model = getPluginMetaData(groupId, artifactId);
        StringBuffer result = new StringBuffer();
        if (model!=null) {
            IssueManagement issueManagement = model.getIssueManagement();
            Scm scm = model.getScm();
            result.append(" h3. Metadata for ");
            result.append(parseString(model.getName()));
            result.append("\n || Group Id | ");
            result.append(getGroupId(model));
            result.append(" || Artifact Id | ");
            result.append(model.getArtifactId());
            result.append("| \n || Latest Release | ");
            result.append(getVersion(model));
            result.append(" || Source Code | ");
            result.append((scm!=null)?parseString(scm.getUrl()):"");
            result.append("| \n || Developers | ");
            result.append(getDeveloperInfo(model.getDevelopers()));
            result.append(" || Issue Tracking | ");
            result.append(getIssueInfo(issueManagement));
            result.append(" | \n ");
            result.append(" h5. Description \n ");
            result.append(" {excerpt:hidden=true} ");
            result.append(parseString(model.getDescription()).replaceAll("\n", " "));
            result.append(" {excerpt} \n ");
            result.append(parseString(model.getDescription()));
            
        } else {
            result.append("{warning}Metadata model not available{warning}");
        }
        return result.toString();
    }
    
    private String getIssueInfo(IssueManagement issueManagement) {
        StringBuffer result = new StringBuffer();
        if (issueManagement!=null) {
            if (issueManagement.getUrl()!=null) {
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
        for (int devCount=0; devCount<developers.size(); devCount++) {
            Developer dev = developers.get(devCount) ;
            String devEmail = null;
            if (devCount!=0) {
                result.append("\n");
            }
            devEmail = dev.getEmail();
            if (devEmail!=null) {
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
        if (temp!=null) {
            result = model.getGroupId();
        } else {
            if (parent!=null) {
                result = parent.getGroupId();
            }
        }
        return result;
    }
    
    private String getVersion(Model model) {
        String result = "";
        String temp = model.getVersion();
        Parent parent = model.getParent();
        if (temp!=null) {
            result = model.getVersion();
        } else {
            if (parent!=null) {
                result = parent.getVersion();
            }
        }
        return result;
    }
    
    private String parseString(String field) {
        if (field==null) {
            return "";
        }
        return field;
    }
    
    /**
     * This method return the displayable name of the model in order of
     *   model name
     *   model groupId
     *   "Not Available" string
     * @param model
     * @return the display name of the model
     */
    private String getName(Model model) {
        String name = parseString(model.getName()).trim();
        if("".equals(name)) {
            name = getGroupId(model);
            if("".equals(name)) {
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
    
    private List<Model> getPluginList(String groupId) throws IOException {
        return new MetadataManager().getMetadatas(groupId);
    }
    
    private Model getPluginMetaData(String groupId, String artifactId) throws IOException {
        return new MetadataManager().getMetadata(groupId, artifactId); 
    }
}
