package Devices_Anthias.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Anthias_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
