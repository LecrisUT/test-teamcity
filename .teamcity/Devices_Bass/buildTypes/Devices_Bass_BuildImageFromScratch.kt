package Devices_Bass.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Bass_BuildImageFromScratch : BuildType({
    templates(Devices.buildTypes.Devices_BuildImageFromScratch)
    name = "Build Image (from scratch)"
})
