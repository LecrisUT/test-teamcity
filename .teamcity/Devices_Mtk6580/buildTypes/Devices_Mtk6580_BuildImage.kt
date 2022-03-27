package Devices_Mtk6580.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Mtk6580_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
