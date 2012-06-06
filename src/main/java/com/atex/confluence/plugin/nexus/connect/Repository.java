package com.atex.confluence.plugin.nexus.connect;

import java.io.Serializable;

/**
 * @author pau
 *
 */
public class Repository implements Serializable {
    
    private static final long serialVersionUID = 9198223708135935739L;
    
    private String repositoryId;
    private String repositoryURL;
    
    /**
     * @return the repositoryId
     */
    public String getRepositoryId() {
        return repositoryId;
    }
    
    /**
     * @param repositoryId the repositoryId to set
     */
    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }
    
    /**
     * @return the repositoryURL
     */
    public String getRepositoryURL() {
        return repositoryURL;
    }
    
    /**
     * @param repositoryURL the repositoryURL to set
     */
    public void setRepositoryURL(String repositoryURL) {
        this.repositoryURL = repositoryURL;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append("[repositoryId: ")
            .append(getRepositoryId())
            .append(", repositoryURL: ")
            .append(getRepositoryURL())
            .append("]");
        
        return buffer.toString();
    }
}
