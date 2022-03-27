package Packages_AsteroidApps_AsteroidHrm

import Packages_AsteroidApps_AsteroidHrm.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidHrm")
    name = "Asteroid Hrm"

    buildType(Packages_AsteroidApps_AsteroidHrm_BuildPackage)

    params {
        param("system.recipeName", "asteroid-hrm")
    }
})
