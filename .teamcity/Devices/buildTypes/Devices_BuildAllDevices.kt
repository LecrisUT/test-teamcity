package Devices.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_BuildAllDevices : BuildType({
    name = "Build All Devices"

    vcs {
        root(_Self.vcsRoots.MetaAsteroid)
    }

    dependencies {
        snapshot(Devices_Anthias.buildTypes.Devices_Anthias_BuildImage) {
        }
        snapshot(Devices_Bass.buildTypes.Devices_Bass_BuildImage) {
        }
        snapshot(Devices_Catfish.buildTypes.Devices_Catfish_BuildImage) {
        }
        snapshot(Devices_Dory.buildTypes.Devices_Dory_BuildImage) {
        }
        snapshot(Devices_Lenok.buildTypes.Devices_Lenok_BuildImage) {
        }
        snapshot(Devices_Mooneye.buildTypes.Devices_Mooneye_BuildImage) {
        }
        snapshot(Devices_Mtk6580.buildTypes.Devices_Mtk6580_BuildImage) {
        }
        snapshot(Devices_Narwhal.buildTypes.Devices_Narwhal_BuildImage) {
        }
        snapshot(Devices_Ray.buildTypes.Devices_Ray_BuildImage) {
        }
        snapshot(Devices_Sawfish.buildTypes.Devices_Sawfish_BuildImage) {
        }
        snapshot(Devices_Skipjack.buildTypes.Devices_Skipjack_BuildImage) {
        }
        snapshot(Devices_Smelt.buildTypes.Devices_Smelt_BuildImage) {
        }
        snapshot(Devices_Sparrow.buildTypes.Devices_Sparrow_BuildImage) {
        }
        snapshot(Devices_Sprat.buildTypes.Devices_Sprat_BuildImage) {
        }
        snapshot(Devices_Sturgeon.buildTypes.Devices_Sturgeon_BuildImage) {
        }
        snapshot(Devices_Swift.buildTypes.Devices_Swift_BuildImage) {
        }
        snapshot(Devices_Tetra.buildTypes.Devices_Tetra_B) {
        }
        snapshot(Devices_Wren.buildTypes.Devices_Wren_BuildImage) {
        }
    }
})
