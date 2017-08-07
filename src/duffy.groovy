// Method for allocating and tearing down duffy resources using https://github.com/cgwalters/centos-ci-skeleton
def duffy(stage, duffyKey = 'duffy-key', repoUrl = 'https://github.com/cgwalters/centos-ci-skeleton',
          subDir = 'cciskel', duffyOps = '--allocate') {

    echo "Currently in stage: ${stage} ${env.DUFFY_OP} resources"
    env.ORIGIN_WORKSPACE = "${env.WORKSPACE}/${stage}"
    env.ORIGIN_BUILD_TAG = "${env.BUILD_TAG}-${stage}"
    env.ORIGIN_CLASS = "builder"
    env.DUFFY_JOB_TIMEOUT_SECS = "3600"
    env.DUFFY_OP = "${duffyOps}"

    if (!(fileExists(subDir))) {
        dir(subDir) {
            git repoUrl
        }
    }

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

// ensure we return 'this' on last line to allow this script to be loaded into flows
return this