package Packages_AsteroidIconsIon

import Packages_AsteroidIconsIon.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidIconsIon")
    name = "Asteroid Icons Ion"
    defaultTemplate = RelativeId("Packages_BuildPackage")

    buildType(Packages_AsteroidIconsIon_BuildPackage)

    params {
        param("system.recipeName", "asteroid-icons-ion")
    }
})
