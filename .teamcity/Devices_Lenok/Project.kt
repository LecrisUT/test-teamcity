package Devices_Lenok

import Devices_Lenok.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Lenok")
    name = "lenok"

    buildType(Devices_Lenok_BuildImage)
    buildType(Devices_Lenok_BuildImageFromScratch)
})
