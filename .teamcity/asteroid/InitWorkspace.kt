package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType

object InitWorkspace : BuildType({

    name = "Initialize workspace"
    description = "Clone sources and initialize workspace"

    enablePersonalBuilds = false
//    maxRunningBuilds = 1

    // Define vcs
    vcs {
        root(CoreVCS.OpenEmbeddedCore, "+:.=>src/oe-core")
        root(CoreVCS.Bitbake, "+:.=>src/oe-core/bitbake")
        root(CoreVCS.MetaOpenEmbedded, "+:.=>src/meta-openembedded")
        root(CoreVCS.MetaSmartwatch, "+:.=>src/meta-smartwatch")
        root(CoreVCS.MetaQt5, "+:.=>src/meta-qt5")
        root(CoreVCS.MetaSmartphone, "+:.=>src/meta-smartphone")
        root(CoreVCS.MetaAsteroid, "+:.=>src/meta-asteroid")

        cleanCheckout = true
    }
})