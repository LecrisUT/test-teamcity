package Devices_Catfish.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_Catfish_BuildImageFromScratch : BuildType({
    templates(Devices.buildTypes.Devices_BuildImageFromScratch)
    name = "Build Image (from scratch)"
    
    disableSettings("RUNNER_5")
})
