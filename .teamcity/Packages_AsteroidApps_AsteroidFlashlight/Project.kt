package Packages_AsteroidApps_AsteroidFlashlight

import Packages_AsteroidApps_AsteroidFlashlight.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidFlashlight")
    name = "Asteroid Flashlight"

    buildType(Packages_AsteroidApps_AsteroidFlashlight_BuildPackage)

    params {
        param("system.recipeName", "asteroid-flashlight")
    }
})
