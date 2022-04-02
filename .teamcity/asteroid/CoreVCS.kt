package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot


object CoreVCS {
    private var fork = DslContext.getParameter("Fork")
    private var upstream = DslContext.getParameter("Upstream")
    var Asteroid = GitVcsRoot {
        id("asteroid/BaseVCS")
        name = "Asteroid"
        url = "git@github.com:$fork/asteroid.git"
        branch = "refs/heads/add-CI"
        authMethod = uploadedKey {
            uploadedKey = "Github Deploy Key"
        }
    }
    var OpenEmbeddedCore = GitVcsRoot {
        id("asteroid/OpenEmbeddedVCS")
        name = "OpenEmbedded Core"
        url = "https://github.com/openembedded/openembedded-core.git"
        branch = "refs/heads/honister"
    }
    var Bitbake = GitVcsRoot {
        id("asteroid/BitBakeVCS")
        name = "Bitbake"
        url = "https://github.com/openembedded/bitbake.git"
        branch = "refs/heads/1.52"
    }
    var MetaOpenEmbedded = GitVcsRoot {
        id("asteroid/MetaOpenEmbeddedVCS")
        name = "Meta OpenEmbedded"
        url = "https://github.com/openembedded/meta-openembedded.git"
        branch = "refs/heads/honister"
    }
    var MetaQt5 = GitVcsRoot {
        id("asteroid/MetaQt5VCS")
        name = "Meta Qt5"
        url = "https://github.com/meta-qt5/meta-qt5"
        branch = "refs/heads/master"
    }
    var MetaSmartphone = GitVcsRoot {
        id("asteroid/MetaSmartphoneVCS")
        name = "Meta Smartphone"
        url = "https://github.com/shr-distribution/meta-smartphone"
        branch = "refs/heads/honister"
    }
    var MetaAsteroid = GitVcsRoot {
        id("asteroid/MetaAsteroidVCS")
        name = "Meta Asteroid"
        url = "https://github.com/$upstream/meta-asteroid"
        branch = "refs/heads/reorganize"
    }
    // TODO: Move MetaSmartwatch outside CoreVCS
    var MetaSmartwatch = GitVcsRoot {
        id("asteroid/MetaSmartwatchVCS")
        name = "Meta Smatwtach"
        url = "https://github.com/$upstream/meta-smartwatch.git"
        branch = "refs/heads/master"
    }
    // TODO: Switch TempRepository to main asteroid VCS
    var TempRepository = GitVcsRoot {
        id("asteroid/SettingsVCS")
        name = "Teamcity Asteroid Settings"
        url = "git@github.com:$fork/teamcity-test"
        branch = "refs/heads/master"
        authMethod = uploadedKey {
            uploadedKey = "Github Deploy Key"
        }
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
}
