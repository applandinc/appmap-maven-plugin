package com.appland.appmap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.appland.appmap.exceptions.ConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

public class PluginLoadTest {

  @Rule
  public MojoRule rule = new MojoRule() {
    @Override
    protected void before() throws Throwable {
    }

    @Override
    protected void after() {
    }
  };

  @Test
  public void testMojoCanBeInstantiated()
      throws Exception {

    Mojo mojo = rule.configureMojo(
        new LoadJavaAppMapAgentMojo(),
        "appmap-maven-plugin",
        new File("target/test-classes/test-project/pom.xml")
    );
    assertNotNull(mojo);
  }

  @Test
  public void testMojoIsLoaded() throws Exception {
    LoadJavaAppMapAgentMojo mojo = (LoadJavaAppMapAgentMojo)
        rule.configureMojo(
            new LoadJavaAppMapAgentMojo(),
            "appmap-maven-plugin",
            new File("target/test-classes/test-project/pom.xml")
        );
    assertNotNull(mojo);
  }

  @Test
  public void testMojoFailsWithMissingDefaultConfigFile() throws Exception {
    LoadJavaAppMapAgentMojo mojo = (LoadJavaAppMapAgentMojo)
        rule.configureMojo(
            new LoadJavaAppMapAgentMojo(),
            "appmap-maven-plugin",
            new File("target/test-classes/test-project/pom.xml")
        );
    assertNotNull(mojo);
    Exception exception = assertThrows(
        MojoExecutionException.class,
        () -> rule.executeMojo(new File("target/test-classes/test-project/"), "prepare-agent")
    );
    assertTrue(exception.getCause() instanceof FileNotFoundException);
  }

  @Test
  public void testMojoFailsWithNotExistingConfigFile() throws Exception {
    LoadJavaAppMapAgentMojo mojo = (LoadJavaAppMapAgentMojo)
        rule.configureMojo(
            new LoadJavaAppMapAgentMojo(),
            "appmap-maven-plugin",
            new File("target/test-classes/test-project/nonexistent_config_file_pom.xml")
        );
    assertNotNull(mojo);
    Exception exception = assertThrows(
        MojoExecutionException.class,
        () -> rule.executeMojo(new File("target/test-classes/test-project/"), "prepare-agent")
    );
    assertTrue(exception.getCause() instanceof FileNotFoundException);
  }

  @Test
  public void testMojoFailsWithMissingConfigFile() throws Exception {
    LoadJavaAppMapAgentMojo mojo = (LoadJavaAppMapAgentMojo)
        rule.configureMojo(
            new LoadJavaAppMapAgentMojo(),
            "appmap-maven-plugin",
            new File("target/test-classes/test-project3/pom.xml")
        );
    assertNotNull(mojo);
    Exception exception = assertThrows(
        MojoExecutionException.class,
        () -> rule.executeMojo(new File("target/test-classes/test-project3/"), "validate-config")
    );
    assertTrue(exception.getCause() instanceof ConfigurationException);
  }

  @Test
  public void testMojoFailsWithNotProperlyConfiguredConfigFile() throws Exception {
    LoadJavaAppMapAgentMojo mojo = (LoadJavaAppMapAgentMojo)
        rule.configureMojo(
            new LoadJavaAppMapAgentMojo(),
            "appmap-maven-plugin",
            new File("target/test-classes/test-project3/pom.xml")
        );
    assertNotNull(mojo);
    Exception exception = assertThrows(
        MojoExecutionException.class,
        () -> rule.executeMojo(new File("target/test-classes/test-project3/"), "validate-config")
    );
    assertTrue(exception.getCause() instanceof ConfigurationException);
  }

}
