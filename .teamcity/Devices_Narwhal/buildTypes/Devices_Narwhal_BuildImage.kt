package Devices_Narwhal.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Narwhal_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
