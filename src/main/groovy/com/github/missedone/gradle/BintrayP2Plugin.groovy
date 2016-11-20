package com.github.missedone.gradle

import org.gradle.api.*

class BintrayP2Plugin implements Plugin<Project> {
    static final String EXTENSION_NAME = 'bintrayP2'

    @Override
    void apply(Project project) {
        BintrayP2Extension extension = project.extensions.create(EXTENSION_NAME, BintrayP2Extension)
        addTasks(project, extension)
    }

    private void addTasks(Project project, BintrayP2Extension extension) {
        project.tasks.withType(AbstractBintrayTask) {
            conventionMapping.apiUrl = { getApiUrl(project, extension) }
            conventionMapping.user = { getBintrayUser(project, extension) }
            conventionMapping.apiKey = { getApiKey(project, extension) }
            conventionMapping.repoOwner = { extension.repoOwner }
            conventionMapping.repoName = { extension.repoName }
            conventionMapping.packageVersion = { extension.packageVersion }
        }

        project.task('publishArtifact', type: BintrayArtifactPublishTask) {
            conventionMapping.packageName = { extension.packageName }
            conventionMapping.artifactFile = { extension.artifactFile }
            conventionMapping.targetPath = { extension.targetPath }
            conventionMapping.override = { extension.override }
        }

        project.task('removeArtifact', type: BintrayArtifactRemoveTask) {
            conventionMapping.targetPath = { extension.targetPath }
        }

        project.task('publishP2Repo', type: BintrayP2RepoPublishTask) {
            conventionMapping.repoDir = { extension.repoDir }
            conventionMapping.zippedRepoFile = { extension.zippedRepoFile }
            conventionMapping.override = { extension.override }
            conventionMapping.compositePackage = { extension.compositePackage }
            conventionMapping.zipSitePackage = { extension.zipSitePackage }
            conventionMapping.updateSitePackage = { extension.updateSitePackage }
        }
    }

    private String getApiUrl(Project project, BintrayP2Extension extension) {
        def apiUrl = extension.apiUrl ?: AbstractBintrayTask.DEFAULT_API_URL
        return apiUrl
    }

    private String getBintrayUser(Project project, BintrayP2Extension extension) {
        def user = extension.user ?: System.getProperty('BINTRAY_USER')
        user = user ?: System.getenv('BINTRAY_USER')
        return user
    }

    private String getApiKey(Project project, BintrayP2Extension extension) {
        def apiKey = extension.apiKey ?: System.getProperty('BINTRAY_API_KEY')
        apiKey = apiKey ?: System.getenv('BINTRAY_API_KEY')
        return apiKey
    }
}
