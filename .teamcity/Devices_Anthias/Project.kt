package Devices_Anthias

import Devices_Anthias.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Anthias")
    name = "anthias"

    buildType(Devices_Anthias_BuildImage)
    buildType(Devices_Anthias_BuildImageFromScratch)
})
