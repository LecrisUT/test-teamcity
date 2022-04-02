package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType

object InitWorkspace : BuildType({
    name = "Initialize workspace"
    description = "Clone sources and initialize workspace"

    enablePersonalBuilds = false
//    maxRunningBuilds = 1

    // Define vcs
    vcs {
        CoreVCS.attachVCS(this)
    }
})