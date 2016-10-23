package org.testng.p2.gradle

import com.jfrog.bintray.gradle.BintrayHttpClientFactory
import groovyx.net.http.HTTPBuilder
import org.gradle.api.GradleException

import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.PUT

class BintrayClient {

    String apiUrl

    String user

    String apiKey

    private HTTPBuilder http

    BintrayClient(String apiUrl, String user, String apiKey) {
        this.apiUrl = apiUrl
        this.user = user
        this.apiKey = apiKey

        assert this.apiKey != null
        assert this.apiUrl != null
        assert this.user != null

        http = BintrayHttpClientFactory.create(apiUrl, user, apiKey)
    }

    /**
     * Upload the content {@code artifactFile} to {@code targetPath} on Bintray
     * 
     * @param artifactFile  the local artifact file to upload
     * @param repoOwner     :subject
     * @param repoName      :repo   
     * @param targetPath    :file_pat
     * @param packageName   :package
     * @param version       :version
     * @param override
     * 
     * @see https://bintray.com/docs/api/#_upload_content
     */
    void uploadContent(File artifactFile, String repoOwner, String repoName, String targetPath,
                       String packageName, String version, boolean override) {
        assert artifactFile != null
        assert repoOwner != null
        assert repoName != null
        assert packageName != null
        assert targetPath != null

        if (!artifactFile.exists()) {
            throw new GradleException("Artifact file [$artifactFile] does not exist.")
        }

        def uploadUri = "/content/${repoOwner}/${repoName}/${targetPath}"

        artifactFile.withInputStream { is ->
            is.metaClass.totalBytes = {
                artifactFile.length()
            }
            println "Uploading to $apiUrl$uploadUri ..."

            // see more in api doc: https://bintray.com/docs/api/#_upload_content
            http.request(PUT) {
                uri.path = uploadUri
                headers.put("X-Bintray-Package", packageName)
                headers.put("X-Bintray-Version", version)
                headers.put("X-Bintray-Publish", "1")
                if (override) {
                    headers.put("X-Bintray-Override", "1")
                }

                // Set the requestContentType to BINARY, so that HTTPBuilder can encode the uploaded file:
                requestContentType = BINARY

                // Set the Content-Type to '*/*' to enable Bintray to set it on its own:
                headers["Content-Type"] = '*/*'

                body = is
                response.success = { resp ->
                    println "Uploaded to [$apiUrl$uri.path]."
                }
                response.failure = { resp, reader ->
                    throw new GradleException("Could not upload to [$apiUrl$uri.path]: $resp.statusLine $reader")
                }
            }
        }
    }

    /**
     * Delete the content on specified {@code targetPath} on Bintray
     * 
     * @param repoOwner     :subject
     * @param repoName      :repo
     * @param targetPath    :file_path
     * 
     * @see https://bintray.com/docs/api/#_delete_content
     */
    void deleteContent(String repoOwner, String repoName, String targetPath) {
        assert repoOwner != null
        assert repoName != null
        assert targetPath != null

        def artifactUri = "/content/${repoOwner}/${repoName}/${targetPath}"
        println "Deleting file $apiUrl$artifactUri ..."

        http.request(DELETE) {
            uri.path = artifactUri

            // Set the Content-Type to '*/*' to enable Bintray to set it on its own:
            headers["Content-Type"] = '*/*'

            response.success = { resp ->
                println "Delete [$apiUrl$uri.path]."
            }
            response.failure = { resp, reader ->
                throw new GradleException("Could not upload to [$apiUrl$uri.path]: $resp.statusLine $reader")
            }
        }
    }

    /**
     * Download the content
     * 
     * @param repoOwner     :subject
     * @param repoName      :repo
     * @param targetPath    :file_path
     * @param location      the local path to store the content 
     * 
     * @see https://bintray.com/docs/api/#_download_content
     */
    void downloadContent(String repoOwner, String repoName, String targetPath, File location) {
        assert repoOwner != null
        assert repoName != null
        assert targetPath != null

        def baseUri = 'https://dl.bintray.com/'
        def artifactUri = "/${repoOwner}/${repoName}/${targetPath}"
        println "Downloading file $baseUri$artifactUri ..."

        def http = BintrayHttpClientFactory.create(baseUri, user, apiKey)
        http.request(GET, BINARY) {
            uri.path = artifactUri

            headers.'Accept' = 'application/octet-stream'

            response.success = { resp, inputStream ->
                new FileOutputStream(location).withStream {
                    it.write(inputStream.getBytes())
                }
            }
            response.failure = { resp, reader ->
                throw new IOException("Could not download [$apiUrl$uri.path]: $resp.statusLine $reader")
            }
        }
    }
}
