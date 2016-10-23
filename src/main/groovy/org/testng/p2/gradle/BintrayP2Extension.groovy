package org.testng.p2.gradle

class BintrayP2Extension {

    // 
    // common properties for the tasks
    //

    String apiUrl

    String user

    String apiKey

    String repoOwner

    String repoName
    
    String packageVersion


    //
    // for artifact publish
    //

    File artifactFile

    String packageName

    String targetPath

    boolean override

    //
    // for p2 repo publish
    //

    String repoDir
    String compositePackage
    String zipSitePackage
    String updateSitePackage
}
