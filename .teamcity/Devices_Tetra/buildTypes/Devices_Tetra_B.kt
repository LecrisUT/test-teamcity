package Devices_Tetra.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Tetra_B : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
