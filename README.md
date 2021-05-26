# Template for creating OpenSearch Plugins
This Repo is a GitHub Template repository ([Learn more about that](https://docs.github.com/articles/creating-a-repository-from-a-template/)).
Using it would create a new repo that is the boilerplate code required for an OpenSearch Plugin. 
This plugin on its own would not add any functionality to OpenSearch, but it is still ready to be installed.
It comes packaged with:
 - Integration tests of two types: Yaml and IntegTest.
 - Notice and License files (Apache License, Version 2.0)
 - A `build.gradle` file supporting this template's current state.


# How To Get Started With OpenSearch Plugins:

## Create your plugin repo using this template
Click on "Use this Template"

![Use this Template](https://docs.github.com/assets/images/help/repository/use-this-template-button.png)

Name the repository, and provide a description. We recommend using the following naming conventions:
- Do not include the word `plugin` in the repo name (e.g. [job-scheduler](https://github.com/opensearch-project/job-scheduler))
- Use lowercase repo names
- Use spinal case for repo names (e.g. [job-scheduler](https://github.com/opensearch-project/job-scheduler))
- do not include the word `OpenSearch` or `OpenSearch Dashboards` in the repo name
- Provide a meaningful description, e.g. `An OpenSearch Dashboards plugin to perform real-time and historical anomaly detection on OpenSearch data`.


## Fix up the template to match your new plugin requirements

This is the file tree structure of the source code, as you can see there are some things you will want to change.

```
`-- src
    |-- main
    |   `-- java
    |       `-- org
    |           `-- opensearch
    |               `-- path
    |                   `-- to
    |                       `-- plugin
    |                           `-- TemplatePlugin.java
    |-- test
    |   `-- java
    |       `-- org
    |           `-- opensearch
    |               `-- path
    |                   `-- to
    |                       `-- plugin
    |                           `-- TemplatePluginIT.java
    `-- yamlRestTest
        |-- java
        |   `-- org
        |       `-- opensearch
        |           `-- path
        |               `-- to
        |                   `-- plugin
        |                       `-- TemplateClientYamlTestSuiteIT.java
        `-- resources
            `-- rest-api-spec
                `-- test
                    `-- hello-world
                        `-- 10_basic.yml

```

### Plugin Name
Now that you have named the repo, you can change the plugin class `TemplatePlugin.java` to have a meaningful name, keeping the `Plugin` suffix.
Change `TemplatePluginIT.java` and `TemplateClientYamlTestSuiteIT.java` accordingly, keeping the `PluginIT` and `ClientYamlTestSuiteIT` suffixes.

### Plugin Path 
Notice these paths in the source tree:
```
-- path
   `-- to
       `-- plugin
```

Let's call this our *plugin path*, as the plugin class would be installed in OpenSearch under that path.
This can be an existing path in OpenSearch, or it can be a unique path for your plugin. We recommend changing it to something meaningful.
Change all these path occurrences to match the path you chose for your plugin.

### Update the `build.gradle` file

Update the following section, using the new repository name and description, plugin class name, and plugin path:

```
def pluginName = 'plugin-template'          // Same as new repo name
def pluginDescription = 'Custom plugin'     // Can be same as new repo description
def pathToPlugin = 'path.to.plugin'         // The path you chose for the plugin
def pluginClassName = 'TemplatePlugin'      // The plugin class name
```

Next update the version of OpenSearch you want the plugin to be installed into. Change the following param:
```
    ext {
        opensearch_version = "1.0.0-beta1" // <-- change this to the version your plugin requires
    }
```

### Update the tests
Notice that in the tests we are checking that the plugin was installed by sending a GET `/_cat/plugins` request to the cluster and expecting `plugin-template` to be in the response.
In order for the tests to pass you must change `plugin-template` in `TemplatePluginIT.java` and in `10_basic.yml` to be the `pluginName` you defined in the `build.gradle` file in the previous section.

### Running the tests
You may need to install OpenSearch and build a local artifact for the integration tests and build tools ([Learn more here](https://github.com/opensearch-project/opensearch-plugins/blob/main/BUILDING.md)):

```
~/OpenSearch (main)> git checkout 1.0.0-beta1 -b beta1-release
~/OpenSearch (main)> ./gradlew publishToMavenLocal -Dbuild.version_qualifier=beta1 -Dbuild.snapshot=false
```

Now you can run all the tests like so:
```
./gradlew check
```
