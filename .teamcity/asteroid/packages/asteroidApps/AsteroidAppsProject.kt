package asteroid.packages.asteroidApps

import asteroid.Settings
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object AsteroidAppsProject : Project({
    id("Packages_AsteroidApps")
    name = "Asteroid Apps"
    description = "Core AsteroidOS packages"

    //  Create the subProjects
    // TODO: Make each package project external
    val packages: List<Project> = Settings.asteroidPackages.map { makePackageProject(it) }
    for (pkg in packages)
        subProject(pkg)
    // TODO: Link to root Asteroid Project for external dependence
})
