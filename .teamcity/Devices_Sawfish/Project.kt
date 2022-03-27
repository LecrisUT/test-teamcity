package Devices_Sawfish

import Devices_Sawfish.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Sawfish")
    name = "sawfish"

    buildType(Devices_Sawfish_BuildImage)
    buildType(Devices_Sawfish_BuildImageFromScratch)
})
