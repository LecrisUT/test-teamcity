package Packages_AsteroidApps_AsteroidTimer

import Packages_AsteroidApps_AsteroidTimer.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidTimer")
    name = "Asteroid Timer"

    buildType(Packages_AsteroidApps_AsteroidTimer_BuildPackage)

    params {
        param("system.recipeName", "asteroid-timer")
    }
})
