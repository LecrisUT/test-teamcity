package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext

object Settings {
	val asteroidPackages = DslContext.getParameter("Packages")
		.split("[\\s,]".toRegex()).toList()
		.filterNot { it.isEmpty() }

//	val communityPackages = DslContext.getParameter("CommunityPackages")
//        .split("[\\s,]".toRegex()).toList()
//        .filterNot { it.isEmpty() }
	val devices = DslContext.getParameter("Devices")
		.split("[\\s,]".toRegex()).toList()
		.filterNot { it.isEmpty() }
	val cleanBuilds = DslContext.getParameter("CleanBuilds","false").toBoolean()
	val withSstate = DslContext.getParameter("WithSstate","true").toBoolean()

	val fork = DslContext.getParameter("Fork")
	val upstream = DslContext.getParameter("Upstream", "AsteroidOS")
	val deploySstate = DslContext.getParameter("DeploySstate", "false").toBoolean()

	object sstateServer {
		val url = DslContext.getParameter("SstateServerURL", "https://sstate.asteroid.org")
		val backendUrl = if (deploySstate)
			DslContext.getParameter("SstateServerBackendURL", "sstate.asteroid.org")
		else null
		val user = if (deploySstate)
			DslContext.getParameter("SstateServerUser", "asteroidos")
		else null
		val location = if (deploySstate)
			DslContext.getParameter("SstateServerLocation", "")
		else null
	}

	// TODO: Change to Github app when available
	val commitStatus = DslContext.getParameter("CommitStatus", "false").toBoolean()
	val commitUser = if (commitStatus)
		DslContext.getParameter("CommitUser", fork)
	else ""
	val pullRequests = DslContext.getParameter("PullRequests", "false").toBoolean()

	init {
		// TODO: Check validity of Token and Ssh key
	}
}