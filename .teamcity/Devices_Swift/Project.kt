package Devices_Swift

import Devices_Swift.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Swift")
    name = "swift"

    buildType(Devices_Swift_BuildImageFromScratch)
    buildType(Devices_Swift_BuildImage)
})
