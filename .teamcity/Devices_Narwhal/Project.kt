package Devices_Narwhal

import Devices_Narwhal.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Narwhal")
    name = "narwhal"

    buildType(Devices_Narwhal_BuildImage)
    buildType(Devices_Narwhal_BuildImageFromScratch)
})
