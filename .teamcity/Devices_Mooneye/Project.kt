package Devices_Mooneye

import Devices_Mooneye.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Mooneye")
    name = "mooneye"

    buildType(Devices_Mooneye_BuildImage)
    buildType(Devices_Mooneye_BuildImageFromScratch)
})
