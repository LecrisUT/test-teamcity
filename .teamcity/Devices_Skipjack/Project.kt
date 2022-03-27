package Devices_Skipjack

import Devices_Skipjack.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices_Skipjack")
    name = "skipjack"

    buildType(Devices_Skipjack_BuildImageFromScratch)
    buildType(Devices_Skipjack_BuildImage)
})
