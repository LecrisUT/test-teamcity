package Devices_Sparrow.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Sparrow_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
