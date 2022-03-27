package Packages_AsteroidApps_AsteroidWeather

import Packages_AsteroidApps_AsteroidWeather.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidWeather")
    name = "Asteroid Weather"

    buildType(Packages_AsteroidApps_AsteroidWeather_BuildPackage)

    params {
        param("system.recipeName", "asteroid-weather")
    }
})
