package Devices_Lenok.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Lenok_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
