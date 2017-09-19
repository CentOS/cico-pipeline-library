#!/usr/bin/env groovy
/**
  Global Shared Utility Methods
  These are the privileged/black listed methods you want to
  carefully control access to
 */

/**
 * Method for aborting all other instances of this job
 * that were still running at the time this method is invoked
 */
def abortPreviousRunningBuilds() {
  def jobname = env.JOB_NAME
  def buildnum = env.BUILD_NUMBER.toInteger()

  def job = Jenkins.instance.getItemByFullName(jobname)
   println("Abort any currently running previous instances")
   for (build in job.builds) {
       def exec = build.getExecutor()

       if (build.number != currentBuild.number && exec != null) {
         exec.interrupt(
          Result.ABORTED,
          new CauseOfInterruption.UserInterruption(
            "Aborted by build #${currentBuild.number}"
          )
        )
        println("Aborted previous running build #${build.number}")
      } else {
        println("Build is not running or is current build, not aborting - #${build.number}")
      }
   }
}
