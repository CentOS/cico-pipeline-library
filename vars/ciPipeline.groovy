import org.centos.Utils

def call(body) {

    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def getDuffy = new Utils()
    try {
        def current_stage = 'cico-pipeline-lib-stage1'
        stage(current_stage) {
            getDuffy.duffyCciskel(current_stage)

        }
        current_stage = 'cico-pipeline-lib-stage2'
        stage(current_stage) {
            getDuffy.duffyCciskel(current_stage)
        }
    } catch (err) {
        echo "Error: Exception from " + current_stage + ":"
        echo e.getMessage()
        throw err
    }
}