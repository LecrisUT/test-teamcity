package Devices_Skipjack.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Skipjack_BuildImageFromScratch : BuildType({
    templates(Devices.buildTypes.Devices_BuildImageFromScratch)
    name = "Build Image (from scratch)"
})
