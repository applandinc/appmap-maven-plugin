package com.appland.appmap;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.FileNotFoundException;

@Mojo(name = "validate-config", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class ValidateConfigMojo extends AppMapAgentMojo {

    @Override
    public void execute() throws MojoExecutionException {
        try {
            if (!isConfigFileValid()) {
                skipMojo(
                        "Skipping AppLand AppMap execution because the config file: "
                                + configFile.getPath()
                                + " does not exist or is not readable"
                );
                throw new FileNotFoundException(configFile.getPath());
            } else {
                getLog().info("Appland AppMap Configuration file found.");
            }
        } catch (Exception e) {
            getLog().error("Error executing AppLand AppMap Java Recorder");
            throw new MojoExecutionException("Error initializing AppLand AppMap Java Recorder", e);
        }
    }
}
