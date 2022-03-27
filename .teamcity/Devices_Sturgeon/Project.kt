package Devices_Sturgeon

import Devices_Sturgeon.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Sturgeon")
    name = "sturgeon"

    buildType(Devices_Sturgeon_BuildImage)
    buildType(Devices_Sturgeon_BuildImageFromScratch)
})
