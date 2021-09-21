package com.appland.appmap.maven;


import org.apache.commons.text.StringEscapeUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * Goal that adds appmap.jar to JVM execution as javaagent,
 * right before the test execution begins.
 */
@Mojo(name = "prepare-agent", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class LoadJavaAppMapAgentMojo extends AppMapAgentMojo {

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

    @Override
    public void execute() {
        if (skip) {
            getLog().info("Skipping AppLand AppMap execution because property 'skip' is set.");
            return;
        }

        getLog().info("Initializing AppLand AppMap Java Recorder." );
        loadAppMapJavaAgent();
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

    private String javaAgentArgLine() {
        return format("\"-javaagent:%s\"", StringEscapeUtils.escapeJava(getAppMapAgentJarPath()));
    }

    /**
     * Generate required quotes JVM argument based on current configuration and
     * prepends it to the given argument command line. If a agent with the same
     * JAR file is already specified this parameter is removed from the existing
     * command line.
     */
    private void removeOldAppMapAgentFromCommandLine(List<String> oldArgs) {
        final String plainAgent = this.javaAgentArgLine();
        oldArgs.removeIf(oldCommand -> oldCommand.startsWith(plainAgent));
    }

    private void addMvnAppMapCommandLineArgsFirst(List<String> args) {
        args.add(this.javaAgentArgLine());

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
}
