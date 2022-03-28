package _Self

import _Self.buildTypes.*
import _Self.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.activeStorage
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.spaceConnection

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
        feature {
            id = "NOVA-1"
            type = "CloudProfile"
            param("clouds.openstack.images", """
                teamcity_image:
                  image: Fedora 35
                  flavor: b2-7-flex
                  network: Lecris
                  security_group: default
            """.trimIndent())
            param("clouds.openstack.instanceCap", "1")
            param("profileServerUrl", "")
            param("system.cloud.profile_id", "NOVA-1")
            param("total-work-time", "")
            param("description", "")
            param("secure:clouds.openstack.password", "credentialsJSON:7e4e1c3a-9c9b-4c7e-876c-fdf8e2ceed91")
            param("clouds.openstack.identity", "user-vccgm6C9ZTaK")
            param("cloud-code", "NOVA")
            param("clouds.openstack.endpointUrl", "https://auth.cloud.ovh.net/v3/")
            param("enabled", "true")
            param("profileId", "NOVA-1")
            param("name", "OVH")
            param("next-hour", "")
            param("terminate-idle-time", "30")
            param("clouds.openstack.zone", "SGP1")
        }
        spaceConnection {
            id = "PROJECT_EXT_2"
            displayName = "JetBrains Space"
            serverUrl = "lecris.jetbrains.space"
            clientId = "a818759e-d905-4460-8b30-bbbc1c754f4a"
            clientSecret = "credentialsJSON:5f080570-56d5-45d0-adc2-e3ba7dd2728b"
        }
        activeStorage {
            id = "PROJECT_EXT_6"
            activeStorageID = "DefaultStorage"
        }
    }

    subProject(Devices.Project)
    subProject(Packages.Project)
    subProject(ThirdPartyDependencies.Project)
})
