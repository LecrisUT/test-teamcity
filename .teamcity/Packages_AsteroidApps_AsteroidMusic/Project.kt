package Packages_AsteroidApps_AsteroidMusic

import Packages_AsteroidApps_AsteroidMusic.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidMusic")
    name = "Asteroid Music"

    buildType(Packages_AsteroidApps_AsteroidMusic_BuildPackage)

    params {
        param("system.recipeName", "asteroid-music")
    }
})
