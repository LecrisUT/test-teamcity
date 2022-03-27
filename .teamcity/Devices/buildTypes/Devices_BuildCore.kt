package Devices.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Devices_BuildCore : BuildType({
    name = "Build Core"

    vcs {
        root(_Self.vcsRoots.Bitbake)
        root(_Self.vcsRoots.MetaAsteroid, "+:. => src/meta-asteroid")
        root(_Self.vcsRoots.MetaOpenEmbedded, "+:. => src/meta-openembedded")
        root(_Self.vcsRoots.MetaQt5, "+:. => src/meta-qt5")
    }
})
