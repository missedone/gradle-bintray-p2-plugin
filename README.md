Gradle Bintray P2 Plugin
====
 
This Gradle plugin is inspired by [Publish an Eclipse p2 composite repository on Bintray](http://www.lorenzobettini.it/2016/02/publish-an-eclipse-p2-composite-repository-on-bintray/) by @LorenzoBettini.
Also the official Bintray API doc is a good reference: https://bintray.com/docs/api/

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
* repoDir - (Optional, File) The location of the P2 repo, default as `project.buildDir`
* compositePackage - (Optional) The Bintray package for composite updatesite, default as `composite`. Please make sure it's created in the Bintray repo before executing the task.
* zipSitePackage - (Optional) The Bintray package for zipped updatesite, default as `zipped`. Please make sure it's created in the Bintray repo before executing the task.
* updateSitePackage - (Optional) The Bintray package for updatesite, default as `updatesites`. Please make sure it's created in the Bintray repo before executing the task.

Example in `build.gradle`:
```
publishP2Repo {
	repoOwner = 'testng-team'
	repoName = 'testng-p2-beta'
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

