package org.testng.p2.gradle

import org.gradle.api.tasks.Input

class BintrayArtifactRemoveTask extends AbstractBintrayTask {

    @Input
    String targetPath

    BintrayArtifactRemoveTask() {
        this.description = 'Remove the artifact from Bintray'
    }

    @Override
    void executeAction() {
        BintrayClient client = new BintrayClient(apiUrl, user, apiKey)
        client.deleteContent(repoOwner, repoName, targetPath)
    }
}
