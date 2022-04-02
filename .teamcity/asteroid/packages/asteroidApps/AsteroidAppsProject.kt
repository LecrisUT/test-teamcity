package asteroid.packages.asteroidApps

import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object AsteroidAppsProject : Project({
    id("Packages_AsteroidApps")
    name = "Asteroid Apps"
    description = "Core AsteroidOS packages"

    // Get the packages and create the subProjects
    // TODO: Make a selectable list in the pom.xml somehow?
    // TODO: Make each package project external
    val packagesNames = DslContext.getParameter("Packages")
        .split("[\\s,]".toRegex()).toList()
        .filterNot { it.isEmpty() }
    val packages: List<Project> = packagesNames.map { makePackageProject(it) }
    for (pkg in packages)
        subProject(pkg)
    // TODO: Link to root Asteroid Project for external dependence
})
