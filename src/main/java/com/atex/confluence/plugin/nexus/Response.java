package com.atex.confluence.plugin.nexus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pau
 *
 */
public class Response {
    
    private List<Artifact> artifacts = new ArrayList<Artifact>();
    private List<Repository> repositories = new ArrayList<Repository>();
    
    public void addArtifact(Artifact artifact) {
        artifacts.add(artifact);
    }
    
    public void addRepository(Repository repository) {
        repositories.add(repository);
    }
    
    public List<Repository> getRepositories() {
        if(repositories == null) {
            repositories = new ArrayList<Repository>();
        }
        return repositories;
    }
    
    public List<Artifact> getArtifacts() {
        if(artifacts == null) {
            artifacts = new ArrayList<Artifact>();
        }
        return artifacts;
    }
    
    public List<Artifact> getReleasedArtifacts() {
        List<Artifact> released = new ArrayList<Artifact>();
        for(Artifact artifact: getArtifacts()) {
            if(!artifact.isSnapshot()) {
                released.add(artifact);
            }
        }
        return released;
    }
    
    public List<Artifact> getLatestReleases() {
        List<Artifact> latest = new ArrayList<Artifact>();
        for(Artifact artifact: getArtifacts()) {
            if(artifact.getVersion().equals(artifact.getLatestRelease())) {
                latest.add(artifact);
            }
        }
        return latest;
    }
    
    
    public Artifact getByArtifactId(String artifactId) {
        for(Artifact artifact: getLatestReleases()) {
            if(artifact.getArtifactId().equals(artifactId)) {
                return artifact;
            }
        }
        // not found
        return null;
    }
}
