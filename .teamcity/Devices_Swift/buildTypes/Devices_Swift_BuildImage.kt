package Devices_Swift.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Swift_BuildImage : BuildType({
    templates(Devices.buildTypes.Devices_BuildImage)
    name = "Build Image"
})
