package Devices_Catfish

import Devices_Catfish.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Catfish")
    name = "catfish"
    description = "TicWatch Pro 2018 (catfish) and TicWatch Pro 2020 (catfish-ext)"

    buildType(Devices_Catfish_BuildImageFromScratch)
    buildType(Devices_Catfish_BuildImage)
})
