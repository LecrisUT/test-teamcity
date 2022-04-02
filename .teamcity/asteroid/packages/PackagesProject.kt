package asteroid.packages

import asteroid.packages.asteroidApps.AsteroidAppsProject
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object PackagesProject : Project({
    id("Packages")
    name = "Packages"
    description = "Package projects"

    subProject(AsteroidAppsProject)
})
