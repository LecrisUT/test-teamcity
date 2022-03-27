package Devices_Sprat

import Devices_Sprat.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Sprat")
    name = "sprat"

    buildType(Devices_Sprat_BuildImage)
    buildType(Devices_Sprat_BuildImageFromScratch)
})
