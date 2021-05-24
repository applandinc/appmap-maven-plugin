package com.appland.appmap;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

public class GoalLoadTest extends AbstractMojoTestCase {

  @Test
  public void testPrepareAgentGoalIsLoadedCorrectly() throws Exception {
    File pluginPom = new File("target/test-classes/test-project2/pom.xml");
    LoadJavaAppMapAgentMojo mojo = (LoadJavaAppMapAgentMojo) lookupMojo("prepare-agent", pluginPom);
    assertNotNull(mojo);
  }

  @Test
  public void testValidateConfigGoalIsLoadedCorrectly() throws Exception {
    File pluginPom = new File("target/test-classes/test-project2/pom.xml");
    ValidateConfigMojo mojo = (ValidateConfigMojo) lookupMojo("validate-config", pluginPom);
    assertNotNull(mojo);
  }
}
