#!/usr/bin/groovy
package org.centos

/**
  * Wrapper function to allocate duffy resources using duffyCciskel
  */
def allocateDuffyCciskel(stage) {
    duffyCciskelOps = [stage:stage, duffyOps:'--allocate']
    duffyCciskel(duffyCciskelOps)
}

/**
 * Wrapper function to teardown duffy resources using duffyCciskel
 */
def teardownDuffyCciskel(stage) {
    duffyCciskelOps = [stage:stage, duffyOps:'--teardown']
    duffyCciskel(duffyCciskelOps)
}

/**
 * Method for allocating and tearing down duffy resources using https://github.com/cgwalters/centos-ci-skeleton
 * Pass a map to the library
 * duffyMap defaults:
 *  duffyMap[stage:'duffyCciskel-stage',
 *           originClass:'builder',
 *           duffyTimeoutSecs:'3600,
 *           duffyOps:'',
 *           subDir:'cciskel',
 *           repoUrl:'https://github.com/cgwalters/centos-ci-skeleton',
 *           duffyKey: 'duffy-key']
 *  duffyKey refers to a secret-file credential setup in Jenkins credentials
 */
def duffyCciskel(duffyMap) {

    env.ORIGIN_WORKSPACE = "${env.WORKSPACE}/${duffyMap.containsKey('stage') ? duffyMap.stage : 'duffyCciskel-stage'}"
    env.ORIGIN_BUILD_TAG = "${env.BUILD_TAG}-${duffyMap.stage}"
    env.ORIGIN_CLASS = "${duffyMap.containsKey('originClass') ? duffyMap.originClass : 'builder'}"
    env.DUFFY_JOB_TIMEOUT_SECS = "${duffyMap.containsKey('duffyTimeoutSecs') ? duffyMap.duffyTimoutSecs : '3600'}"
    env.DUFFY_OP = "${duffyMap.containsKey('duffyOps') ? duffyMap.duffyOps : ''}"
    echo "Currently in stage: ${duffyMap.stage} ${env.DUFFY_OP} resources"
    subDir = duffyMap.containsKey('subDir') ? duffyMap.subDir : 'cciskel'

    if (! (fileExists(subDir)) ){
        dir(subDir) {
            git duffyMap.containsKey('repoUrl') ? duffyMap.repoUrl : 'https://github.com/cgwalters/centos-ci-skeleton'
        }
    }

    withCredentials([file(credentialsId: duffyMap.containsKey('duffyKey') ? duffyMap.duffyKey : 'duffy-key',
            variable: 'DUFFY_KEY')]) {
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
                    exec ${WORKSPACE}/cciskel/cciskel-duffy ${DUFFY_OP} --prefix=ci-pipeline \
                        --class=${ORIGIN_CLASS} --jobid=${ORIGIN_BUILD_TAG} \
                        --timeout=${DUFFY_JOB_TIMEOUT_SECS:-0} --count=${DUFFY_COUNT:-1}
                else
                    exec ${WORKSPACE}/cciskel/cciskel-duffy ${DUFFY_OP}
                fi
                exit
        '''
    }
}

/**
 * Convert bash shell properties to groovy
 * shellFile - Pass a shell formatted properties file
 */
def convertProps(shellFile) {
    def command = $/awk -F'=' '{print "env."$1"=\""$2"\""}' ${shellFile} > ${shellFile}.groovy/$
    sh command

    return "${shellFile}.groovy"
}

// ensure we return 'this' on last line to allow this script to be loaded into flows
return this