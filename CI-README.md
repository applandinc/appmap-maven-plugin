# Prerequisites

1. Read the CI-README from `appmap-java` repo which describes organisational setup 
(accounts etc.), and also explains manual check&promotion process of Maven releases in the OSSRH

# Configuration

Any Gradle settings could be configured either as:
* `org.gradle.project.foo` property
* `ORG_GRADLE_PROJECT_foo` environment variable

In Travis, it's recommended to use repo-level environment variables.

To set up multiline variable (e.g. for GPG key) in Travis, 
use converter `bin/multiline_input_to_travis_env.py` from the appmap-java repo.

This converter consumes multiline data on standard input, and emits escaped 
bash expression on the stdout. The result could be sored in Travis UI.

# Parameters

## Environment variables (for dev environments):

* `TRAVIS_BRANCH` -- version
* `TRAVIS_REPO_SLUG` (optional) -- set up if you're using git fork (default: `applandinc/appmap-java-maven-plugin`)

## Gradle settings

* `ossrhUsername` -- triggers publication to remote repo (otherwise artifact are stored under `./build/repo`
* `ossrhPassword` 
* `signingKey` -- ascii-armored text contents of GPG key (for Travis use converted variable). If omitted, signatures are not generated (use in local publishing only!)
* `signingPassword`
* `artifactName`, `artifactDescription` -- unlikely you will change them
* `publicationGroupId` -- optional (default: `com.appland`)
* `publicationArtifactId` -- optional (default: `appmap-maven-plugin`)
* `mavenRepo` -- optional (default: `https://s01.oss.sonatype.org`)
* `paramVersion` -- version number (alternative to `TRAVIS\_BRANCH` env variable; has higher precendence)

# Publication

## Manual

`./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository`

## CI

upon commit to `master` branch, semantic-release bumps version (if needed) 
and uses Gradle Nexus Publishing plugin to perform full set of steps 
required to propagate release to Maven Central repo via OSSRH
