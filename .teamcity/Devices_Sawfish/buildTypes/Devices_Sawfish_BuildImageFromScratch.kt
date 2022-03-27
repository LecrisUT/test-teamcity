package Devices_Sawfish.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Sawfish_BuildImageFromScratch : BuildType({
    templates(Devices.buildTypes.Devices_BuildImageFromScratch)
    name = "Build Image (from scratch)"
})
