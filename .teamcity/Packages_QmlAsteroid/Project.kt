package Packages_QmlAsteroid

import Packages_QmlAsteroid.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_QmlAsteroid")
    name = "Qml Asteroid"
    defaultTemplate = RelativeId("Packages_BuildPackage")

    buildType(Packages_QmlAsteroid_BuildPackage)

    params {
        param("system.recipeName", "qml-asteroid")
    }
})
