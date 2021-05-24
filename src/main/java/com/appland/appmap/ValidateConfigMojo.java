package com.appland.appmap;

import com.appland.appmap.exceptions.ConfigurationException;
import java.io.FileNotFoundException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Mojo that validates the appmap agent config file.
 */
@Mojo(name = "validate-config", defaultPhase = LifecyclePhase.TEST_COMPILE)
public class ValidateConfigMojo extends AppMapAgentMojo {

  @Override
  public void execute() throws MojoExecutionException {
    try {
      if (!isConfigFileValid()) {
        skipMojo(
            "Skipping AppLand AppMap execution because the config file: "
                + configFile.getPath()
                + " does not exist or is not properly configured"
        );
        if (configFile.exists()) {
          throw new ConfigurationException(configFile.getPath());
        } else {
          throw new FileNotFoundException(configFile.getPath());
        }
      } else {
        getLog().info("Appland AppMap Configuration file found.");
      }
    } catch (Exception e) {
      getLog().error("Error executing AppLand AppMap Java Recorder");
      throw new MojoExecutionException("Error initializing AppLand AppMap Java Recorder", e);
    }
  }
}
