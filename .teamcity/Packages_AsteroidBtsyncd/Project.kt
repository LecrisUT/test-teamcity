package Packages_AsteroidBtsyncd

import Packages_AsteroidBtsyncd.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidBtsyncd")
    name = "Asteroid Btsyncd"
    defaultTemplate = RelativeId("Packages_BuildPackage")

    buildType(Packages_AsteroidBtsyncd_BuildPackage)

    params {
        param("system.recipeName", "asteroid-btsyncd")
    }
})
