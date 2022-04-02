package asteroid.devices

import asteroid.CoreVCS
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

// TODO: Make a separate TeamCity vcsSettings in MetaSmartwatch
object DevicesProject : Project({
    id("Devices")
    name = "Devices"
    description = "Device projects"

    // Get the devices and create the subProjects
    // TODO: Make a selectable list in the pom.xml somehow?
    // TODO: Make each device project external
    val devicesNames = DslContext.getParameter("Devices")
        .split("[\\s,]".toRegex()).toList()
        .filterNot { it.isEmpty() }
    val devices :List<Project> = devicesNames.map { makeDeviceProject(it) }
    for (device in devices)
        subProject(device)
    // TODO: Link to root Asteroid Project for external dependence

    // Attach MetaSmartwatch vcs
    // TODO: Move MetaSmartwatch outside CoreVCS
    vcsRoot(CoreVCS.MetaSmartwatch)
})
