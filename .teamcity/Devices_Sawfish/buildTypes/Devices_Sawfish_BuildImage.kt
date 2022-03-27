package Devices_Sawfish.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Sawfish_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
