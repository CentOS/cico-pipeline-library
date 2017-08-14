import org.centos.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getUtils = new Utils()
    def pipelineProps = ''
    try {
        def current_stage = 'cico-pipeline-lib-stage1'
        stage(current_stage) {
            writeFile file: "${env.WORKSPACE}/job.properties",
                    text: "MYVAR1=test\n" +
                          "MYVAR2=3\n"
        }
        current_stage = 'cico-pipeline-lib-stage2'
        stage(current_stage) {
            // Convert a classic shell properties file to groovy format to be loaded
            pipelineProps = getUtils.convertProps("${env.WORKSPACE}/job.properties")
            // Load these as environment variables into the pipeline
            load(pipelineProps)
            sh '''
                echo "Original Shell Properties File:"
                cat ${WORKSPACE}/job.properties
                echo ""
                echo "Groovy Properties File:"
                cat ${WORKSPACE}/job.properties.groovy
                echo "Environment Variables"
                env
            '''
        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo e.getMessage()
        throw err
    }
}