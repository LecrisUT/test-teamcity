package Devices_Wren.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Wren_BuildImageFromScratch : BuildType({
    templates(Devices.buildTypes.Devices_BuildImageFromScratch)
    name = "Build Image (from scratch)"
})
