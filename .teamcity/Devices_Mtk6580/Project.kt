package Devices_Mtk6580

import Devices_Mtk6580.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Mtk6580")
    name = "mtk6580"

    buildType(Devices_Mtk6580_BuildImage)
    buildType(Devices_Mtk6580_BuildImageFromScratch)
})
