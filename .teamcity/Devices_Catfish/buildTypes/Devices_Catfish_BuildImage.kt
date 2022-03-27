package Devices_Catfish.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Catfish_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
