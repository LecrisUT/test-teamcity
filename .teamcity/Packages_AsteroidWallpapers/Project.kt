package Packages_AsteroidWallpapers

import Packages_AsteroidWallpapers.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidWallpapers")
    name = "Asteroid Wallpapers"
    defaultTemplate = RelativeId("Packages_BuildPackage")

    buildType(Packages_AsteroidWallpapers_BuildPackage)

    params {
        param("system.recipeName", "asteroid-wallpapers")
    }
})
