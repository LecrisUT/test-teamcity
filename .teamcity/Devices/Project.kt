package Devices

import Devices.buildTypes.*
import Devices.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Devices")
    name = "Devices"
    description = "Device specific projects"

    vcsRoot(Devices_MetaSmatwtach)

    buildType(Devices_BuildCore)
    buildType(Devices_RebuildAllDevices)
    buildType(Devices_BuildAllDevices)

    template(Devices_BuildImage)
    template(Devices_BuildImageFromScratch)

    params {
        param("system.MACHINE", "%system.teamcity.projectName%")
    }

    cleanup {
        keepRule {
            disabled = true
            id = "KEEP_RULE_1"
            keepAtLeast = allBuilds()
            dataToKeep = statisticsOnly()
            applyPerEachBranch = true
            preserveArtifactsDependencies = true
        }
    }

    subProject(Devices_Mtk6580.Project)
    subProject(Devices_Skipjack.Project)
    subProject(Devices_Ray.Project)
    subProject(Devices_Bass.Project)
    subProject(Devices_Tetra.Project)
    subProject(Devices_Sturgeon.Project)
    subProject(Devices_Dory.Project)
    subProject(Devices_Sparrow.Project)
    subProject(Devices_Mooneye.Project)
    subProject(Devices_Anthias.Project)
    subProject(Devices_Sawfish.Project)
    subProject(Devices_Swift.Project)
    subProject(Devices_Catfish.Project)
    subProject(Devices_Wren.Project)
    subProject(Devices_Narwhal.Project)
    subProject(Devices_Sprat.Project)
    subProject(Devices_Lenok.Project)
    subProject(Devices_Smelt.Project)
})
