package asteroid.packages

import asteroid.Settings
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object CommunityAppsProject : Project({
	id("Packages_CommuityApps")
	name = "Asteroid Comunity Apps"
	description = "Additional community app packages"
}) {
	//  Create the subProjects
//	val packages = Settings.asteroidPackages.map { PackageProject(it) }
//
//	init {
//		// TODO: Make each package project external
//		for (pkg in packages)
//			subProject(pkg)
//		// TODO: Link to root Asteroid Project for external dependence
//	}
}
