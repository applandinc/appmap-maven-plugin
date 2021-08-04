package com.appland.appmap;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.*;

import static java.lang.String.format;

public abstract class AppMapAgentMojo extends AbstractMojo {

    static final String APPMAP_AGENT_GROUP_ID = "com.appland";
    static final String APPMAP_AGENT_ARTIFACT_ID = "appmap-agent";
    static final String SUREFIRE_ARG_LINE = "argLine";
    static final String DEFAULT_CONFIG_FILE = "appmap.yml";
    static final List<String> DEBUG_FLAGS = Arrays.asList("debug", "hooks", "locals", "http");

    @Parameter(property = "skip")
    protected boolean skip = false;

    @Parameter(property = "project.outputDirectory")
    protected File outputDirectory = new File("target/appmap");

    @Parameter(property = "project.configFile")
    protected String configFile = DEFAULT_CONFIG_FILE;

    @Parameter(property = "project.debug")
    protected String debug = "info";

    @Parameter(property = "project.debugFile")
    protected File debugFile = new File("target/appmap/agent.log");

    @Parameter(property = "project.eventValueSize")
    protected Integer eventValueSize = 1024;

    @Parameter(property = "project")
    private MavenProject project;

    public abstract void execute() throws MojoExecutionException;

    protected void skipMojo() {
    }

    protected void loadAppMapJavaAgent() {
        final String newValue = buildArguments();
        setProjectArgLineProperty(newValue);
        getLog().info(SUREFIRE_ARG_LINE
            + " set to " + StringEscapeUtils.unescapeJava(newValue));
    }

    /**
     * This method builds the needed parameter to run the Agent, if previous configuration is found is also attached in
     * the SUREFIRE_ARG_LINE, if previous version of the AppMap agent is found is removed and replaced with the version
     * of this maven plugin
     *
     * @return formatted and escaped arguments to run on command line
     */
    private String buildArguments() {
        List<String> args = new ArrayList<>();
        final String oldConfig = getCurrentArgLinePropertyValue();
        if (oldConfig != null) {
            final List<String> oldArgs = new ArrayList<>(Arrays.asList(oldConfig.split(" ")));
            removeOldAppMapAgentFromCommandLine(oldArgs);
            args.addAll(oldArgs);
        }
        addMvnAppMapCommandLineArgsFirst(args);
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg).append(" ");
        }
        return builder.toString();
    }

    /**
     * Generate required quotes JVM argument based on current configuration and
     * prepends it to the given argument command line. If a agent with the same
     * JAR file is already specified this parameter is removed from the existing
     * command line.
     */
    private void removeOldAppMapAgentFromCommandLine(List<String> oldArgs) {
        final String plainAgent = format("-javaagent:%s", getAppMapAgentJarPath());
        oldArgs.removeIf(oldCommand -> oldCommand.startsWith(plainAgent));
    }

    private void addMvnAppMapCommandLineArgsFirst(List<String> args) {
        args.add(StringEscapeUtils.escapeJava(
            format("-javaagent:%s", getAppMapAgentJarPath())
        ));

        if (this.debug != null && !this.debug.isEmpty()) {
            final List<String> debugTokens = new ArrayList<>(Arrays.asList(this.debug.split("[,|\\s]")));
            boolean hasDebug = false;
            for (String token : debugTokens) {
                if (DEBUG_FLAGS.contains(token)) {
                    hasDebug = true;
                    if (token.equals("debug")) {
                        args.add("-Dappmap.debug");
                    } else {
                        args.add("-Dappmap.debug." + token);
                    }
                }
            }

            if (hasDebug) {
                args.add(0, "-Dappmap.debug.file=" + StringEscapeUtils.escapeJava(format("%s", debugFile)));
            }
        }

        args.add(0, "-Dappmap.output.directory=" + StringEscapeUtils.escapeJava(format("%s", outputDirectory)));
        if (!configFile.equals(DEFAULT_CONFIG_FILE)) {
            args.add(0, "-Dappmap.config.file=" + StringEscapeUtils.escapeJava(format("%s", configFile)));
        }
        args.add(0, "-Dappmap.event.valueSize=" + eventValueSize);
    }


    private void setProjectArgLineProperty(String newValue) {
        project.getProperties().setProperty(SUREFIRE_ARG_LINE, newValue);
    }

    private String getCurrentArgLinePropertyValue() {
        return project.getProperties().getProperty(SUREFIRE_ARG_LINE);
    }

    protected String getAppMapAgentJarPath() {
        Set<Artifact> artifacts = project.getArtifacts();
        Optional<Artifact> appmapAgentArtifact = artifacts
            .stream()
            .filter(a -> {
                return a.getArtifactId().equals(APPMAP_AGENT_ARTIFACT_ID) &&
                    a.getGroupId().equals(APPMAP_AGENT_GROUP_ID);
            }).findFirst();
        if (appmapAgentArtifact.isPresent()) {
            return appmapAgentArtifact.get().getFile().getPath();
        } else {
            throw new RuntimeException("Artifact groupId=com.appland artifactId=appmap-agent is not present in the project; please add it to the pom.xml");
        }
    }
}
