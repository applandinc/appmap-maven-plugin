# AppMap Maven plugin

- [AppMap Maven plugin](#appmap-maven-plugin)
  - [Quickstart](#quickstart)
  - [About](#about)
  - [Plugin goals](#plugin-goals)
  - [Plugin configuration](#plugin-configuration)
  - [Notes](#notes)
    - [Configuring Surefire](#configuring-surefire) 
  - [Troubleshooting](#troubleshooting)
  - [Running without modifying `pom.xml`](#running-without-modifying-pomxml)

## Quickstart

First, ensure you have a
[properly configured `appmap.yml`](https://github.com/applandinc/appmap-java#configuration)
in your root project directory. A basic configuration may look like:

```yml
# appmap.yml
name: my_organization/my_application

packages:
  # List the packages you'd like to record here.
  - path: com.myorganization.myapplication

  # Individual classes or methods can be chosen as well.
  #
  # - path: com.myorganization.myapplication.MyClass
  # - path: com.myorganization.myapplication.MyClass#myInstanceMethod
  # - path: com.myorganization.myapplication.MyClass.myStaticMethod
  #
  # Optionally, include paths of packages, classes
  # or methods that you'd like to exclude from recording
  #
  # - exclude: com.myorganization.myapplication.util
  # - exclude: com.myorganization.myapplication.MyClass
  # - exclude: com.myorganization.myapplication.MyClass#myInstanceMethod
  # - exclude: com.myorganization.myapplication.MyClass.myStaticMethod
```

Next, add the following plugin definition to your `pom.xml`:
```xml
<!-- the plugin element goes to build/plugins -->
<!-- AppMap agent, default parameters -->
<plugin>
    <groupId>com.appland</groupId>
    <artifactId>appmap-maven-plugin</artifactId>
    <executions>
        <execution>
            <phase>process-test-classes</phase>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

That's all! The AppMap agent will automatically record your tests when you run
`mvn test`. By default, AppMap files are output to `target/appmap`.

Using Visual Studio Code? [Download the AppMap extension](https://marketplace.visualstudio.com/items?itemName=appland.appmap)
to view AppMap files in your IDE.

## About

The AppMap Maven Plugin provides simple method for recording AppMaps in running
tests in Maven projects and a seamless integration into CI/CD pipelines. The
recording agent requires `appmap.yml` configuration file, see
[appmap-java](https://github.com/applandinc/appmap-java/blob/master/README.md)
for details.

## Plugin goals

- `prepare-agent` - adds the AppMap Java agent to the JVM

## Plugin configuration

- `configFile` Path to the `appmap.yml` config file. Default: _./appmap.yml_
- `outputDirectory` Output directory for `.appmap.json` files. Default:
  _./target/appmap_
- `skip` Agent won't record tests when set to true. Default: _false_
- `debug` Enable debug flags as a comma separated list. Accepts: `info`,
  `hooks`, `http`, `locals` Default: _info_
- `debugFile` Specify where to output debug logs. Default:
  _target/appmap/agent.log_
- `eventValueSize` Specifies the length of a value string before truncation
  occurs. If set to 0, truncation is disabled. Default: _1024_
  
## Notes
### Configuring Surefire
Some configuration parameters of the Surefire plugin may prevent the appmap plugin
from being activated when the tests are run:
1. `forkCount` may not be set to `0`. Please set it to a value larger than `0` or
remove this configuration parameter from `pom.xml`
3. If `argLine` is specified, it must include `@{argLine}`

Example:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven-surefire-plugin.version}</version>
    <configuration>
        <forkCount>1</forkCount>
        <reuseForks>true</reuseForks>
        <argLine>
            @{argLine} --illegal-access=permit
        </argLine>
    </configuration>
</plugin>
```

## Troubleshooting

**I have no `target/appmap` directory**  
  It's likely that the agent is not running. Double check the `prepare-agent`
  goal is being run. If the JVM is being forked at any point, make sure the
  `javaagent` argument is being propagated to the new process. Additionally
  check that the Surefire plugin configuration is not preventing the agent
  from running. See ["Configuring Surefire"](#configuring-surefire) for more
  information.

**`*.appmap.json` files are present, but appear empty or contain little data**  
  Double check your `appmap.yml`. This usually indicates that the agent is
  functioning as expected, but no classes or methods referenced in the
  `appmap.yml` configuration are being executed. You may need to adjust the
  packages being recorded. Follow this link for more information:
  https://github.com/applandinc/appmap-java#configuration

**My tests aren't running or I'm seeing `The forked VM terminated without
  properly saying goodbye.`**  
  Check the agent log (defaults to `target/appmap/agent.log`) and/or the
  Maven Surefire dumpstream (`target/surefire-reports/${DATETIME}.dumpstream`).
  This is typically indicative of an invalid `appmap.yml` configuration.

**I have a test failure that only occurs while the agent is attached**  
  Please open an issue at [applandinc/appmap-java](https://github.com/applandinc/appmap-java/issues).
  Attach a link to the source code or repository (if available), as well as any
  other relevant information including:
  - the contents of `appmap.yml`
  - the run command used (such as `mvn test`)
  - output of the run command
  - any Maven Surefire dumpstreams generated
    (`target/surefire-reports/${DATETIME}.dumpstream`)

## Running without modifying `pom.xml`
By specifying the fully-qualified goal, the agent can be run without any
additional configuration:
```sh
mvn com.appland:appmap-maven-plugin:prepare-agent test
```
