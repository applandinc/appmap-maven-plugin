package com.appland.appmap.maven;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.*;

public abstract class AppMapAgentMojo extends AbstractMojo {

    static final String APPMAP_AGENT_ARTIFACT_NAME = "com.appland:appmap-agent";

    @Parameter(property = "plugin.artifactMap")
    protected Map<String, Artifact> pluginArtifactMap;

    @Parameter(property = "project")
    protected MavenProject project;

    public abstract void execute() throws MojoExecutionException;

    protected String getAppMapAgentJarPath() {
        return pluginArtifactMap.get(APPMAP_AGENT_ARTIFACT_NAME).getFile().getPath();
    }
}
