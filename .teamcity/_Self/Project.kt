package _Self

import _Self.buildTypes.*
import _Self.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.activeStorage

object Project : Project({
    description = "Base Asteroid Project"

    vcsRoot(Bitbake)
    vcsRoot(MetaOpenEmbedded)
    vcsRoot(MetaSmartphone)
    vcsRoot(OpenEmbeddedCore)
    vcsRoot(MetaAsteroidApps)
    vcsRoot(Asteroid)
    vcsRoot(MetaQt5)
    vcsRoot(MetaAsteroid)

    buildType(Requirements_1)

    params {
        text("system.sstate.server.location", "/var/www/asteroidos", label = "Sstate server location", description = "Path location of the sstate-cache", readOnly = true, allowEmpty = true)
        text("system.sstate.server.user", "asteroidos", label = "SState server user", description = "Username used to upload sstate-cache", readOnly = true, allowEmpty = true)
        text("system.sstate.server.upload_address", "192.168.0.2", label = "SState server backend address", description = "Backend adress to upload the sstate-cache", readOnly = true, allowEmpty = false)
        text("system.sstate.server.address", "https://asteroidos.lecris.dev", label = "SState server public address", description = "Public address serving sstate-cache", readOnly = true, allowEmpty = false)
        text("system.MACHINE", "qemux86", label = "Machine", description = "Device for which to build", allowEmpty = false)
        select("system.architecture", "armv7vehf-neon", label = "Architecture", description = "CPU architecture for which to build",
                options = listOf("ARMv7" to "armv7vehf-neon"))
    }

    features {
        activeStorage {
            id = "PROJECT_EXT_6"
            activeStorageID = "DefaultStorage"
        }
    }

    subProject(Devices.Project)
    subProject(Packages.Project)
    subProject(ThirdPartyDependencies.Project)
})
