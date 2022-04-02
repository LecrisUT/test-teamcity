package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.RelativeId
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.activeStorage

object AsteroidProject : Project({
    description = "Base Asteroid Project"

    // Attach vcsRoots from CoreVCS
    for (vcs in CoreVCS.all())
        if (vcs.id?.equals(RelativeId("MetaSmartwatchVCS")) == false)
            vcsRoot(vcs)

    // Attach InitWorkspace build
    buildType(InitWorkspace)

    // Attach the subProjects
    // TODO: Link to subprojects externally
    subProject(asteroid.devices.DevicesProject)
    subProject(asteroid.packages.PackagesProject)
    subProject(asteroid.thirdparty.ThirdPartyProject)

    // Define the build parameters inherited by the subprojects
    params {
        text(
            "system.sstate.server.location",
            "/var/www/asteroidos",
            label = "Sstate server location",
            description = "Path location of the sstate-cache",
            readOnly = true, allowEmpty = true
        )
        text(
            "system.sstate.server.user",
            "asteroidos",
            label = "SState server user",
            description = "Username used to upload sstate-cache",
            readOnly = true,
            allowEmpty = true
        )
        text(
            "system.sstate.server.upload_address",
            "192.168.0.2",
            label = "SState server backend address",
            description = "Backend adress to upload the sstate-cache",
            readOnly = true,
            allowEmpty = false
        )
        text(
            "system.sstate.server.address",
            "https://asteroidos.lecris.dev",
            label = "SState server public address",
            description = "Public address serving sstate-cache",
            readOnly = true,
            allowEmpty = false
        )
        select(
            "system.architecture",
            "armv7vehf-neon",
            label = "Architecture",
            description = "CPU architecture for which to build",
            options = listOf("ARMv7" to "armv7vehf-neon")
        )
    }

    features {
        activeStorage {
            id = "PROJECT_EXT_6"
            activeStorageID = "DefaultStorage"
        }
    }

})
