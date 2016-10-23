package org.testng.p2.gradle

import groovy.xml.QName
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

class BintrayP2RepoPublishTask extends AbstractBintrayTask {

    @InputDirectory
    @Optional
    File repoDir

    @Input
    @Optional
    String compositePackage

    @Input
    @Optional
    String zipSitePackage

    @Input
    @Optional
    String updateSitePackage

    @Input
    @Optional
    boolean override

    BintrayP2RepoPublishTask() {
        this.description = 'Publish the P2 repo to Bintray, including zipped updatesite as well.'
        this.group = 'publishing'
    }

    @Override
    void executeAction() {
        if (repoDir == null) {
            repoDir = getProject().buildDir
        }
        if (compositePackage == null) {
            compositePackage = 'composite'
        }
        if (zipSitePackage == null) {
            zipSitePackage = 'zipped'
        }
        if (updateSitePackage == null) {
            updateSitePackage = 'updatesites'
        }
        assert repoDir.exists():
                '''repo dir ${repoDir} does not exist'''

        def updateSiteDir = new File(repoDir, 'updatesite')
        assert updateSiteDir.exists():
                '''updatesite dir ${updateSiteDir} does not exist'''
        def zippedSiteFile = new File(repoDir, 'updatesite.zip')
        assert zippedSiteFile.exists():
                '''zipped updatesite file ${zippedSiteFile} does not exist'''

        if (packageVersion == null) {
            packageVersion = parsePackageVersion(updateSiteDir)
        }
        assert packageVersion != null :
                '''package version may not be null'''

        BintrayClient client = new BintrayClient(apiUrl, user, apiKey)
        uploadUpdateSite(client, updateSiteDir, updateSitePackage)
        uploadZippedSite(client, zippedSiteFile)
        updateCompositeUpdateSite(client)
    }

    private void uploadUpdateSite(BintrayClient client, File updateSiteDir, String updateSitePackage) {
        logger.lifecycle("Uploading updatesite ${updateSiteDir} ...")
        def tree = getProject().fileTree(dir: updateSiteDir)
        tree.visit {element ->
            logger.debug("$element.relativePath => $element.file")
            if (element.file.isFile()) {
                def packageName = updateSitePackage
                def targetPath = "${packageName}/${packageVersion}/$element.relativePath"
                client.uploadContent(element.file, repoOwner, repoName, targetPath,
                                        packageName, packageVersion, override)
            }
        }
    }

    private void uploadZippedSite(BintrayClient client, File zippedSiteFile) {
        logger.lifecycle("Uploading zipped updatesite ${zippedSiteFile} ...")
        def packageName = zipSitePackage
        def targetPath = "${packageName}/${packageVersion}/" + zippedSiteFile.name
        client.uploadContent(zippedSiteFile, repoOwner, repoName, targetPath, 
                                packageName, packageVersion, override)
    }

    private void updateCompositeUpdateSite(BintrayClient client) {
        def targetPath = "${compositePackage}/compositeContent.xml"
        def localFile = new File(repoDir, 'compositeContent.xml')
        try {
            client.downloadContent(repoOwner, repoName, targetPath, localFile)
        } catch (IOException e) {
            logger.error(e.getMessage())
        }

        Node rootNode = null
        if (localFile.exists() && localFile.isFile()) {
            rootNode = new XmlParser().parse(localFile)
        }
        else {
            def xmlText = """<?xml version='1.0' encoding='UTF-8'?>
<?compositeMetadataRepository version='1.0.0'?>
<repository name='TestNG Composite P2 Repo' type='org.eclipse.equinox.internal.p2.metadata.repository.CompositeMetadataRepository' version='1.0.0'>
    <properties size='2'>
        <property name='p2.timestamp' value='1454086165279'/>
        <property name='p2.atomic.composite.loading' value='true'/>
    </properties>
    <children size='3'>
    </children>
</repository>"""
            rootNode = new XmlParser().parseText(xmlText)
        }

        def subsiteLoc = "../${updateSitePackage}/${packageVersion}"
        rootNode.'children'[0].appendNode("child", [location: subsiteLoc])
        new XmlNodePrinter(new PrintWriter(new FileWriter(localFile))).print(rootNode)

        logger.lifecycle("Uploading composite updatesite ...")
        targetPath = "${compositePackage}/compositeContent.xml"
        client.uploadContent(localFile, repoOwner, repoName, targetPath,
                                compositePackage, packageVersion, override)
        targetPath = "${compositePackage}/compositeArtifacts.xml"
        client.uploadContent(localFile, repoOwner, repoName, targetPath,
                                compositePackage, packageVersion, override)
    }

    private String parsePackageVersion(File updateSiteDir) {
        String ver = null
        def featuresDir = new File(updateSiteDir, "features")
        if (featuresDir.exists() && featuresDir.directory) {
            def tree = getProject().fileTree(dir: featuresDir)
            tree.include('org.testng.p2.feature_*.jar')
            tree.visit {element ->
                def str = element.file.name
                str = str.substring('org.testng.p2.feature_'.length())
                str = str.substring(0, str.length() - '.jar'.length())
                def idx = str.indexOf('-')
                if (idx > 0) {
                    str = str.substring(0, idx)
                }
                ver = str
            }
        }
        return ver
    }
}