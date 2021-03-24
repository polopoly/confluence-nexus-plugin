package com.atex.confluence.plugin.nexus.data;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author pau
 *
 */
public class Artifact implements Serializable {
    
    private static final long serialVersionUID = 3663063958124774761L;
    
    private String groupId;
    private String artifactId;
    private String version;
    private String type;
    private String classifier;
    private String repositoryId;
    private String url;
    private String latestSnapshot;
    private String latestRelease;
    private String latestReleaseRepositoryId;
    private String latestSnapshotRepositoryId;
    
    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }
    
    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    /**
     * @return the artifactId
     */
    public String getArtifactId() {
        return artifactId;
    }
    
    /**
     * @param artifactId the artifactId to set
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    
    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(final String classifier) {
        this.classifier = classifier;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(final String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * @return the latestSnapshot
     */
    public String getLatestSnapshot() {
        return latestSnapshot;
    }
    
    /**
     * @param latestSnapshot the latestSnapshot to set
     */
    public void setLatestSnapshot(String latestSnapshot) {
        this.latestSnapshot = latestSnapshot;
    }
    
    /**
     * @return the latestRelease
     */
    public String getLatestRelease() {
        return latestRelease;
    }
    
    /**
     * @param latestRelease the latestRelease to set
     */
    public void setLatestRelease(String latestRelease) {
        this.latestRelease = latestRelease;
    }
    
    /**
     * @return the latestReleaseRepositoryId
     */
    public String getLatestReleaseRepositoryId() {
        return latestReleaseRepositoryId;
    }
    
    /**
     * @param latestReleaseRepositoryId the latestReleaseRepositoryId to set
     */
    public void setLatestReleaseRepositoryId(String latestReleaseRepositoryId) {
        this.latestReleaseRepositoryId = latestReleaseRepositoryId;
    }
    
    /**
     * @return the latestSnapshotRepositoryId
     */
    public String getLatestSnapshotRepositoryId() {
        return latestSnapshotRepositoryId;
    }
    
    /**
     * @param latestSnapshotRepositoryId the latestSnapshotRepositoryId to set
     */
    public void setLatestSnapshotRepositoryId(String latestSnapshotRepositoryId) {
        this.latestSnapshotRepositoryId = latestSnapshotRepositoryId;
    }
    
    public boolean isSnapshot() {
        return getVersion() == null? false: getVersion().contains("SNAPSHOT");
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        
        buffer.append("[groupId: ")
            .append(getGroupId())
            .append(", artifactId: ")
            .append(getArtifactId())
            .append(", version: ")
            .append(getVersion())
            .append(", latestSnapshotRepositoryId: ")
            .append(getLatestSnapshotRepositoryId())
            .append(", latestReleaseRepositoryId: ")
            .append(getLatestReleaseRepositoryId())
            .append(", latestRelease: ")
            .append(getLatestRelease())
            .append("]");
        
        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Artifact) {
            Artifact another = (Artifact) obj;
            if(getGroupId().equals(another.getGroupId()) 
                    && getArtifactId().equals(another.getArtifactId()) 
                    && getVersion().equals(another.getVersion())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {getGroupId(), getArtifactId(), getVersion()});
    }
}
