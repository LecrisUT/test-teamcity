package Devices_Bass

import Devices_Bass.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Bass")
    name = "bass"

    buildType(Devices_Bass_BuildImage)
    buildType(Devices_Bass_BuildImageFromScratch)
})
