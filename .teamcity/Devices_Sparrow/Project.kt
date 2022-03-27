package Devices_Sparrow

import Devices_Sparrow.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Sparrow")
    name = "sparrow"

    buildType(Devices_Sparrow_BuildImage)
    buildType(Devices_Sparrow_BuildImageFromScratch)
})
