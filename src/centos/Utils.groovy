#!/usr/bin/groovy
package org.centos


/**
 * Method for allocating and tearing down duffy resources using https://github.com/cgwalters/centos-ci-skeleton
 * duffyOps can be '--allocate', '--teardown', and '--no-op'
 */
def duffy(stage,  duffyOps = '--allocate', duffyKey = 'duffy-key',
          repoUrl = 'https://github.com/cgwalters/centos-ci-skeleton', subDir = 'cciskel') {

    env.ORIGIN_WORKSPACE = "${env.WORKSPACE}/${stage}"
    env.ORIGIN_BUILD_TAG = "${env.BUILD_TAG}-${stage}"
    env.ORIGIN_CLASS = "builder"
    env.DUFFY_JOB_TIMEOUT_SECS = "3600"
    env.DUFFY_OP = "${duffyOps}"
    echo "Currently in stage: ${stage} ${env.DUFFY_OP} resources"

    if (! (fileExists(subDir)) ){
        dir(subDir) {
            git repoUrl
        }
    }

    if (duffyOps != "--no-op"){

        withCredentials([file(credentialsId: duffyKey, variable: 'DUFFY_KEY')]) {
            sh '''
                    #!/bin/bash
                    set -xeuo pipefail
            
                    cp ${DUFFY_KEY} ~/duffy.key
                    chmod 600 ~/duffy.key
        
                    mkdir -p ${ORIGIN_WORKSPACE}
                    # If we somehow got called without an op, do nothing.
                    if test -z "${DUFFY_OP:-}"; then
                      exit 0
                    fi
                    if test -n "${ORIGIN_WORKSPACE:-}"; then
                      pushd ${ORIGIN_WORKSPACE}
                    fi
                    if test -n "${ORIGIN_CLASS:-}"; then
                        exec ${WORKSPACE}/cciskel/cciskel-duffy ${DUFFY_OP} --prefix=ci-pipeline --class=${ORIGIN_CLASS} \
                            --jobid=${ORIGIN_BUILD_TAG} --timeout=${DUFFY_JOB_TIMEOUT_SECS:-0} --count=${DUFFY_COUNT:-1}
                    else
                        exec ${WORKSPACE}/cciskel/cciskel-duffy ${DUFFY_OP}
                    fi
                    exit
                '''
        }
    }
}

/**
 * Convert bash shell properties to groovy
 */
def convertProps(file1, file2) {
    def command = $/awk -F'=' '{print "env."$1"=\""$2"\""}' ${file1} > ${file2}/$
    sh command
}

// ensure we return 'this' on last line to allow this script to be loaded into flows
return this