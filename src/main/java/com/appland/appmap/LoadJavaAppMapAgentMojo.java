package com.appland.appmap;


import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.FileNotFoundException;
import java.nio.file.Files;

/**
 * Goal that adds appmap.jar to JVM execution as javaagent, right before the test execution begins.
 */
@Mojo(name = "prepare-agent", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class LoadJavaAppMapAgentMojo extends AppMapAgentMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            if (skip) {
                skipMojo("Skipping AppLand AppMap execution because property skip is set.");
                return;
            } else if (!isConfigFileValid()) {
                skipMojo(
                        "Skipping AppLand AppMap execution because the config file: "
                                + configFile.getPath()
                                + " does not exist or is not readable"
                );
                throw new FileNotFoundException(configFile.getPath());
            } else {
                getLog().info("Initializing AppLand AppMap Java Recorder.");
                loadAppMapJavaAgent();
            }
        } catch (Exception e) {
            getLog().error("Error initializing AppLand AppMap Java Recorder");
            throw new MojoExecutionException("Error initializing AppLand AppMap Java Recorder", e);
        }
    }

    private boolean isConfigFileValid() {
        return configFile.exists() && Files.isReadable(configFile.toPath());
    }
}
