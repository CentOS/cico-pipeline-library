import org.centos.OpenshiftUtils

/**
 * Library of Openshift Utils using the Openshift Client Jenkins Plugin
 *
 * Required Plugin: https://github.com/openshift/jenkins-client-plugin
**/

class openshiftUtils implements Serializable {

    def openshiftUtils = new OpenshiftUtils()

    /**
     * Figures out current OpenShift namespace (project name). That is the project
     * the Jenkins is running in.
     *
     * The method assumes that the Jenkins instance has kubernetes plugin installed
     * and properly configured.
     */
    def getOpenshiftNamespace() {
        return openshiftUtils.getOpenshiftNamespace()
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
        return openshiftUtils.getOpenshiftDockerRegistryURL()
    }


}