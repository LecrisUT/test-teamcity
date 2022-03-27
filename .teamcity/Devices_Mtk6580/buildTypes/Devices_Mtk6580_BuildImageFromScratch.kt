package Devices_Mtk6580.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Mtk6580_BuildImageFromScratch : BuildType({
    templates(Devices.buildTypes.Devices_BuildImageFromScratch)
    name = "Build Image (from scratch)"
})
