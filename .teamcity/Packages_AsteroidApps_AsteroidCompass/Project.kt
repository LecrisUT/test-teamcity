package Packages_AsteroidApps_AsteroidCompass

import Packages_AsteroidApps_AsteroidCompass.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidCompass")
    name = "Asteroid Compass"

    buildType(Packages_AsteroidApps_AsteroidCompass_BuildPackage)

    params {
        param("system.recipeName", "asteroid-compass")
    }
})
