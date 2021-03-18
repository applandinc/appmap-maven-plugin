package com.appland.appmap;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class PluginLoadTest {
    static final String SUREFIRE_ARG_LINE = "argLine";

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
    public void testMojoIsLoaded()
            throws Exception {
        LoadJavaAppMapAgentMojo mojo = (LoadJavaAppMapAgentMojo)
                rule.configureMojo(
                        new LoadJavaAppMapAgentMojo(),
                        "appmap-maven-plugin",
                        new File("target/test-classes/test-project/pom.xml")
                );
        assertNotNull(mojo);
        rule.executeMojo(new File("target/test-classes/test-project/"), "prepare-agent");

        assertNotNull(System.getProperty(SUREFIRE_ARG_LINE));
        assertEquals(System.getProperty(SUREFIRE_ARG_LINE), "");
    }

    @Test
    public void testMojoLoaded2() throws Exception {
        File target = new File("target/test-classes/test-project/", "target");
        if (target.exists()) FileUtils.cleanDirectory(target);

        MavenProject project = rule.readMavenProject(new File("/"));
        Artifact artifact = new DefaultArtifact("com.appland", "appmap-agent", "0.5.0", "test", "type", "classifier", null );
        artifact.setFile(new File("pom.xml"));
        project.addAttachedArtifact(artifact);
        rule.configureMojo(new LoadJavaAppMapAgentMojo(), "appmap-maven-plugin", new File("target/test-classes/test-project/pom.xml"));
        rule.executeMojo(project, "prepare-agent");
        assertNotNull(project.getProperties().getProperty(SUREFIRE_ARG_LINE));
        assertTrue(project.getProperties().getProperty(SUREFIRE_ARG_LINE).contains("appmap-maven-plugin"));
    }


}
