package Devices_Dory.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Dory_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
