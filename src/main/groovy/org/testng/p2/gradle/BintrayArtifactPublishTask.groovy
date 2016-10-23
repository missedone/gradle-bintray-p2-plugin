package org.testng.p2.gradle

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional

class BintrayArtifactPublishTask extends AbstractBintrayTask {

    @InputFile
    File artifactFile

    @Input
    String packageName

    @Input
    @Optional
    String targetPath

    @Input
    @Optional
    boolean override

    BintrayArtifactPublishTask() {
        this.description = 'Publishes artifact to bintray.com.'
        this.group = 'publishing'
    }

    @Override
    void executeAction() {
        BintrayClient client = new BintrayClient(apiUrl, user, apiKey)
        client.uploadContent(artifactFile, repoOwner, repoName, targetPath, packageName, packageVersion, override)
    }
}