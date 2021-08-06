package com.appland.appmap.maven;

import org.apache.maven.plugins.annotations.Mojo;

/**
 * Goal that prints the path to the appmap-agent.jar.
 */
@Mojo(name = "print-jar-path")
public class PrintAppMapAgentJarPathMojo extends AppMapAgentMojo {
    @Override
    public void execute() {
        System.out.printf("java.home=%s\n", System.getProperty("java.home"));
        System.out.printf("%s.jar.path=%s\n", APPMAP_AGENT_ARTIFACT_NAME, getAppMapAgentJarPath());
    }
}
