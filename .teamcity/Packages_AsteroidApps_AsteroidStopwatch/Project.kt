package Packages_AsteroidApps_AsteroidStopwatch

import Packages_AsteroidApps_AsteroidStopwatch.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidStopwatch")
    name = "Asteroid Stopwatch"

    buildType(Packages_AsteroidApps_AsteroidStopwatch_BuildPackage)

    params {
        param("system.recipeName", "asteroid-stopwatch")
    }
})
