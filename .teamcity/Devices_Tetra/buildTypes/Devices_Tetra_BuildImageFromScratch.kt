package Devices_Tetra.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Tetra_BuildImageFromScratch : BuildType({
    templates(Devices.buildTypes.Devices_BuildImageFromScratch)
    name = "Build Image (from scratch)"
})
