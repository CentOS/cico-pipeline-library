#!/usr/bin/groovy
package org.centos


/**
 * Figures out current OpenShift namespace (project name). That is the project
 * the Jenkins is running in.
 *
 * The method assumes that the Jenkins instance has kubernetes plugin installed
 * and properly configured.
 */
def getOpenshiftNamespace() {
    return openshift.withCluster() {
        openshift.project()
    }
}

/**
 * Figures out the Docker registry URL which is supposed to host all the images
 * for current OpenShift project.
 *
 * The method assumes that all images in the current project are stored in the
 * internal Docker registry. This is not 100% bullet proof, but should be good
 * enough as starting point.
 */
def getOpenshiftDockerRegistryURL() {
    return openshift.withCluster() {
        def someImageUrl = openshift.raw(
                "get imagestream -o=jsonpath='{.items[0].status.dockerImageRepository}'").out
        String[] urlParts = someImageUrl.split('/')

        // there should be three parts in the image url:
        // <docker-registry-url>/<namespace>/<image-name:tag>
        if (urlParts.length != 3) {
            throw new IllegalStateException(
                    "Can not determine Docker registry URL!" +
                            " Unexpected image URL: $someImageUrl" +
                            " - expecting the URL in the following format:" +
                            " '<docker-registry-url>/<namespace>/<image-name:tag>'.")
        }

        def registryUrl = urlParts[0]
        registryUrl
    }
}
