package Packages_AsteroidApps_AsteroidCalculator

import Packages_AsteroidApps_AsteroidCalculator.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidCalculator")
    name = "Asteroid Calculator"

    buildType(Packages_AsteroidApps_AsteroidCalculator_BuildPackage)

    params {
        param("system.recipeName", "asteroid-calculator")
    }
})
