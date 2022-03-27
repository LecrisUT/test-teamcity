package Devices_Wren.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Wren_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
