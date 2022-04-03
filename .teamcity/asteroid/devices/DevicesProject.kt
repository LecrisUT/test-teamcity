package asteroid.devices

import asteroid.CoreVCS
import asteroid.Settings
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

// TODO: Make a separate TeamCity vcsSettings in MetaSmartwatch
object DevicesProject : Project({
    id("Devices")
    name = "Devices"
    description = "Device projects"

    // Create the subProjects
    // TODO: Make each device project external
    // TODO: Add a map of device -> architecture
    val devices :List<Project> = Settings.devices.map { makeDeviceProject(it) }
    for (device in devices)
        subProject(device)
    // TODO: Link to root Asteroid Project for external dependence

    // Attach MetaSmartwatch vcs
    // TODO: Move MetaSmartwatch outside CoreVCS
    vcsRoot(CoreVCS.MetaSmartwatch)
})
