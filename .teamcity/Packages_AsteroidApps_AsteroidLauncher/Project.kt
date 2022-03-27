package Packages_AsteroidApps_AsteroidLauncher

import Packages_AsteroidApps_AsteroidLauncher.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidLauncher")
    name = "Asteroid Launcher"

    buildType(Packages_AsteroidApps_AsteroidLauncher_BuildPackage)

    params {
        param("system.recipeName", "asteroid-launcher")
    }
})
