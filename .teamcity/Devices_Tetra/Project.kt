package Devices_Tetra

import Devices_Tetra.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Tetra")
    name = "tetra"

    buildType(Devices_Tetra_BuildImageFromScratch)
    buildType(Devices_Tetra_B)
})
