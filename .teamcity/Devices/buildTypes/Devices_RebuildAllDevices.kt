package Devices.buildTypes

import Devices.vcsRoots.Devices_MetaSmatwtach
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object Devices_RebuildAllDevices : BuildType({
    name = "Rebuild All Devices"
    description = "Rebuild all devices from scratch and upload a new cache seed"

    vcs {
        root(Devices.vcsRoots.Devices_MetaSmatwtach)
    }

    triggers {
        vcs {
            enabled = false
            triggerRules = """
                +:root=${Devices_MetaSmatwtach.id};comment=\[Rebuild\]:**
                +:root=Asteroid_HttpsGithubComAsteroidOSMetaAsteroid;comment=\[Rebuild\]:**
            """.trimIndent()

            branchFilter = ""
        }
    }

    dependencies {
        snapshot(Devices_Anthias.buildTypes.Devices_Anthias_BuildImageFromScratch) {
        }
        snapshot(Devices_Bass.buildTypes.Devices_Bass_BuildImageFromScratch) {
        }
        snapshot(Devices_Catfish.buildTypes.Devices_Catfish_BuildImageFromScratch) {
            runOnSameAgent = true
            onDependencyCancel = FailureAction.ADD_PROBLEM
        }
        snapshot(Devices_Dory.buildTypes.Devices_Dory_BuildImageFromScratch) {
        }
        snapshot(Devices_Lenok.buildTypes.Devices_Lenok_BuildImageFromScratch) {
        }
        snapshot(Devices_Mooneye.buildTypes.Devices_Mooneye_BuildImageFromScratch) {
        }
        snapshot(Devices_Mtk6580.buildTypes.Devices_Mtk6580_BuildImageFromScratch) {
        }
        snapshot(Devices_Narwhal.buildTypes.Devices_Narwhal_BuildImageFromScratch) {
        }
        snapshot(Devices_Ray.buildTypes.Devices_Ray_BuildImageFromScratch) {
        }
        snapshot(Devices_Sawfish.buildTypes.Devices_Sawfish_BuildImageFromScratch) {
        }
        snapshot(Devices_Skipjack.buildTypes.Devices_Skipjack_BuildImageFromScratch) {
        }
        snapshot(Devices_Smelt.buildTypes.Devices_Smelt_BuildImageFromScratch) {
        }
        snapshot(Devices_Sparrow.buildTypes.Devices_Sparrow_BuildImageFromScratch) {
        }
        snapshot(Devices_Sprat.buildTypes.Devices_Sprat_BuildImageFromScratch) {
        }
        snapshot(Devices_Sturgeon.buildTypes.Devices_Sturgeon_BuildImageFromScratch) {
        }
        snapshot(Devices_Swift.buildTypes.Devices_Swift_BuildImageFromScratch) {
        }
        snapshot(Devices_Tetra.buildTypes.Devices_Tetra_BuildImageFromScratch) {
        }
        snapshot(Devices_Wren.buildTypes.Devices_Wren_BuildImageFromScratch) {
        }
    }
})
