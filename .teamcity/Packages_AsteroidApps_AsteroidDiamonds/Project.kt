package Packages_AsteroidApps_AsteroidDiamonds

import Packages_AsteroidApps_AsteroidDiamonds.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidDiamonds")
    name = "Asteroid Diamonds"

    buildType(Packages_AsteroidApps_AsteroidDiamonds_BuildPackage)

    params {
        param("system.recipeName", "asteroid-diamonds")
    }
})
