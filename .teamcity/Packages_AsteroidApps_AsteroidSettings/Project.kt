package Packages_AsteroidApps_AsteroidSettings

import Packages_AsteroidApps_AsteroidSettings.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidSettings")
    name = "Asteroid Settings"

    buildType(Packages_AsteroidApps_AsteroidSettings_BuildPackage)

    params {
        param("system.recipeName", "asteroid-settings")
    }
})
