package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsSettings
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot


object CoreVCS {
    private var fork = DslContext.getParameter("Fork")
    private var upstream = DslContext.getParameter("Upstream","AsteroidOS")
    var Asteroid = GitVcsRoot {
        id("AsteroidVCS")
        name = "Asteroid"
        url = "https://github.com/$fork/asteroid.git"
        branch = "refs/heads/master"
    }
    var OpenEmbeddedCore = GitVcsRoot {
        id("OpenEmbeddedVCS")
        name = "OpenEmbedded Core"
        url = "https://github.com/openembedded/openembedded-core.git"
        branch = "refs/heads/honister"
    }
    var Bitbake = GitVcsRoot {
        id("BitBakeVCS")
        name = "Bitbake"
        url = "https://github.com/openembedded/bitbake.git"
        branch = "refs/heads/1.52"
    }
    var MetaOpenEmbedded = GitVcsRoot {
        id("MetaOpenEmbeddedVCS")
        name = "Meta OpenEmbedded"
        url = "https://github.com/openembedded/meta-openembedded.git"
        branch = "refs/heads/honister"
    }
    var MetaQt5 = GitVcsRoot {
        id("MetaQt5VCS")
        name = "Meta Qt5"
        url = "https://github.com/meta-qt5/meta-qt5"
        branch = "refs/heads/master"
    }
    var MetaSmartphone = GitVcsRoot {
        id("MetaSmartphoneVCS")
        name = "Meta Smartphone"
        url = "https://github.com/shr-distribution/meta-smartphone"
        branch = "refs/heads/honister"
    }
    var MetaAsteroid = GitVcsRoot {
        id("MetaAsteroidVCS")
        name = "Meta Asteroid"
        url = "https://github.com/$upstream/meta-asteroid"
        branch = "refs/heads/master"
    }
    // TODO: Move MetaSmartwatch outside CoreVCS
    var MetaSmartwatch = GitVcsRoot {
        id("MetaSmartwatchVCS")
        name = "Meta Smatwtach"
        url = "https://github.com/$upstream/meta-smartwatch.git"
        branch = "refs/heads/master"
    }
    // TODO: Switch TempRepository to main asteroid VCS
    var TempRepository = GitVcsRoot {
        id("SettingsVCS")
        name = "Teamcity Asteroid Settings"
        url = "https://github.com/LecrisUT/test-teamcity"
        branch = "refs/heads/lecris.dev"
    }
    fun all(): List<VcsRoot>{
        return listOf(
            Asteroid,
            OpenEmbeddedCore,
            Bitbake,
            MetaOpenEmbedded,
            MetaQt5,
            MetaSmartphone,
            MetaAsteroid,
            MetaSmartwatch,
            TempRepository
        )
    }
    fun attachVCS(init: VcsSettings, forDevice: Boolean = false) {
        init.root(OpenEmbeddedCore, "+:.=>src/oe-core")
        init.root(Bitbake, "+:.=>src/oe-core/bitbake")
        init.root(MetaOpenEmbedded, "+:.=>src/meta-openembedded")
        init.root(MetaQt5, "+:.=>src/meta-qt5")
        init.root(MetaSmartphone, "+:.=>src/meta-smartphone")
        init.root(MetaAsteroid, "+:.=>src/meta-asteroid")
        if (forDevice)
            init.root(MetaSmartwatch, "+:.=>src/meta-smartwatch")

        init.cleanCheckout = true
    }
}
