package Devices_Ray

import Devices_Ray.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Ray")
    name = "ray"

    buildType(Devices_Ray_BuildImageFromScratch)
    buildType(Devices_Ray_BuildImage)
})
