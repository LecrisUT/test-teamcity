package Packages_AsteroidApps_AsteroidCamera

import Packages_AsteroidApps_AsteroidCamera.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidCamera")
    name = "Asteroid Camera"

    buildType(Packages_AsteroidApps_AsteroidCamera_BuildPackage)

    params {
        param("system.recipeName", "asteroid-calculator")
    }
})
