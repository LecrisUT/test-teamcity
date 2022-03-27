package Packages_AsteroidApps_AsteroidCalendar

import Packages_AsteroidApps_AsteroidCalendar.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidCalendar")
    name = "Asteroid Calendar"

    buildType(Packages_AsteroidApps_AsteroidCalendar_BuildPackage)

    params {
        param("system.recipeName", "asteroid-calendar")
    }
})
