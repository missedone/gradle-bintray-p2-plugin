Gradle Bintray P2 Plugin
====

[![Bintray](https://api.bintray.com/packages/missedone/gradle-plugins/gradle-bintray-p2-plugin/images/download.svg)](https://bintray.com/missedone/gradle-plugins/gradle-bintray-p2-plugin/_latestVersion)

This Gradle plugin is inspired by [Publish an Eclipse p2 composite repository on Bintray](http://www.lorenzobettini.it/2016/02/publish-an-eclipse-p2-composite-repository-on-bintray/) by @LorenzoBettini.
Also the official Bintray API doc is a good reference: https://bintray.com/docs/api/

## Usage

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.github.missedone:gradle-bintray-p2-plugin:1.2.0'
    }
}

apply plugin: 'bintray-p2'
```

### Tasks

* publishP2Repo - Publish the P2 repo to Bintray, including zipped updatesite as well.
* publishArtifact - Publishes artifact to bintray.com.
* removeArtifact - Remove the artifact from Bintray

#### publishP2Repo

Publish the local P2 repo to Bintray with the following layout:
```
repoOwner (could be personal or organization)
|-- updatesites
|   |-- 1.0.0.v2016...
|   |   |-- artifacts.jar
|   |   |-- content.jar
|   |   |-- features
|   |   |   |-- your feature.jar
|   |   |-- plugins
|   |   |   |-- your bundle.jar
|   ...
|-- composite
|   |-- compositeContent.xml
|   |-- compositeArtifacts.xml
|-- zipped
    |-- your site 1.0.0.v2016....zip
    |-- your site 1.1.0.v2016....zip
    ...
```

Task Properties:
* apiUrl - (Optional) Default as 'https://api.bintray.com'
* user - (Required) The Bintray user name, seek order: task properties -> system properties -> environment variables.
* apiKey - (Required) The user's api key, seek order: task properties -> system properties -> environment variables.
* repoOwner - (Required) The repo owner
* repoName - (Required) The repo name
* packageVersion - (Optional) Default get from the generated updatesite under `project.buildDir`
* mainFeatureId - (Optional) The main feature id, used to parse the package version if `packageVersion` is not specified. by default it uses the first feature under `${repoDir}/features`.
* repoDir - (Optional, File) The location of the P2 repo, default as `project.buildDir/updatesite`
* zippedRepoFile - (Optional, File) the location of the zipped repo file, default as `project.buildDir/updatesite.zip`
* compositePackage - (Optional) The Bintray package for composite updatesite, default as `composite`. **Please make sure it's created in the Bintray repo before executing the task**.
* zipSitePackage - (Optional) The Bintray package for zipped updatesite, default as `zipped`. **Please make sure it's created in the Bintray repo before executing the task**.
* updateSitePackage - (Optional) The Bintray package for updatesite, default as `updatesites`. **Please make sure it's created in the Bintray repo before executing the task**.
* subCompositeStrategy - (Optional) the sub composite version strategy 'MAJOR', 'MINOR', 'MICRO', 'NONO'; default as 'NONE'; with this option, it will auto create a sub-compoiste update site named as the version defined by the strategy, the top composite updatesite will reference to this sub site.

Example in `build.gradle`:
```
publishP2Repo {
	repoOwner = 'testng-team'
	repoName = 'testng-eclipse'

	// set `compositePackage` an empty stirng to store the compoiste update site (compositeContent.xml, compositeArtifacts.xml) to the repo root, for example: http://dl.bintray.com/testng-team/testng-eclipse/
	compositePackage = ''

    repoDir = new File("target/site")
    zippedRepoFile = new File("target/site_assembly.zip")
}
```

#### publishArtifact

Task Properties:
* apiUrl - (Optional) Default as 'https://api.bintray.com'
* user - (Required) The Bintray user name, seek order: task properties -> system properties -> environment variables.
* apiKey - (Required) The user's api key, seek order: task properties -> system properties -> environment variables.
* repoOwner - (Required) The repo owner
* repoName - (Required) The repo name
* artifactFile - (Required, File) The artifact file to be uploaded
* packageName - (Required) The Bintray package to store the artifact
* packageVersion - (Required) The package version 
* targetPath - (Required) The relative target path to the package on Bintray

#### removeArtifact

Task Properties:
* apiUrl - (Optional) Default as 'https://api.bintray.com'
* user - (Required) The Bintray user name, seek order: task properties -> system properties -> environment variables.
* apiKey - (Required) The user's api key, seek order: task properties -> system properties -> environment variables.
* repoOwner - (Required) The repo owner
* repoName - (Required) The repo name
* targetPath - (Required) The relative target path to the package on Bintray

## Example:

* TestNG Eclipse [beta updatesite](http://dl.bintray.com/testng-team/testng-eclipse/), [build.gradle](https://github.com/cbeust/testng-eclipse/blob/master/testng-eclipse-update-site/build.gradle)
* TestNG P2 [beta updatesite](http://dl.bintray.com/testng-team/testng-p2-beta/), [build.gradle](https://github.com/testng-team/testng-p2/blob/master/build.gradle)
