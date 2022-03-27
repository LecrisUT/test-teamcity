package Devices_Wren

import Devices_Wren.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Wren")
    name = "wren"

    buildType(Devices_Wren_BuildImage)
    buildType(Devices_Wren_BuildImageFromScratch)
})
