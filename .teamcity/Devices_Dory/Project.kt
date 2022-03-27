package Devices_Dory

import Devices_Dory.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Dory")
    name = "dory"

    buildType(Devices_Dory_BuildImageFromScratch)
    buildType(Devices_Dory_BuildImage)
})
