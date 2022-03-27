package Devices_Smelt

import Devices_Smelt.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Smelt")
    name = "smelt"

    buildType(Devices_Smelt_BuildImage)
    buildType(Devices_Smelt_BuildImageFromScratch)
})
