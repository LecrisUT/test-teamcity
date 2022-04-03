package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext

object Settings {
    val asteroidPackages = DslContext.getParameter("Packages")
        .split("[\\s,]".toRegex()).toList()
        .filterNot { it.isEmpty() }
//    val communityPackages = DslContext.getParameter("CommunityPackages")
//        .split("[\\s,]".toRegex()).toList()
//        .filterNot { it.isEmpty() }
    val devices = DslContext.getParameter("Devices")
        .split("[\\s,]".toRegex()).toList()
        .filterNot { it.isEmpty() }

    val fork = DslContext.getParameter("Fork")
    val upstream = DslContext.getParameter("Upstream","AsteroidOS")
    val deploySstate = DslContext.getParameter("DeploySstate", "false").toBoolean()

    // TODO: Change to Github app when available
    val commitStatus = DslContext.getParameter("CommitStatus", "false").toBoolean()
    val commitUser = DslContext.getParameter("CommitUser", fork)
    val pullRequests = DslContext.getParameter("PullRequests", "false").toBoolean()
    init {
        // TODO: Check validity of Token and Ssh key
    }
}