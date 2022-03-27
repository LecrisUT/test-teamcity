package Packages_AsteroidApps.buildTypes

import Packages.vcsRoots.Packages_PackageSource
import _Self.vcsRoots.MetaAsteroid
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object Packages_AsteroidApps_BuildPackage : Template({
    name = "Build Package"
    description = "Build a specific recipe"

    enablePersonalBuilds = false
    maxRunningBuilds = 1
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(_Self.vcsRoots.OpenEmbeddedCore, "+:.=>src/oe-core")
        root(_Self.vcsRoots.Bitbake, "+:.=>src/oe-core/bitbake")
        root(_Self.vcsRoots.MetaOpenEmbedded, "+:.=>src/meta-openembedded")
        root(_Self.vcsRoots.MetaQt5, "+:.=>src/meta-qt5")
        root(_Self.vcsRoots.MetaSmartphone, "+:.=>src/meta-smartphone")
        root(_Self.vcsRoots.MetaAsteroid, "+:.=>src/meta-asteroid")
        root(Packages.vcsRoots.Packages_PackageSource, "+:. => src/%system.recipeName%")

        showDependenciesChanges = true
    }

    steps {
        script {
            name = "Prepare config files"
            id = "RUNNER_2"
            scriptContent = """
                mkdir -p build/conf
                echo 'DISTRO = "asteroid"
                MACHINE = "%system.MACHINE%"
                PACKAGE_CLASSES = "package_ipk"
                SSTATE_MIRRORS ?= " \
                    file://.* %system.sstate.server.address%/%system.MACHINE%/sstate-cache/PATH;downloadfilename=PATH \n \
                    file://.* %system.sstate.server.address%/%system.architecture%/sstate-cache/PATH;downloadfilename=PATH \n \
                    file://.* %system.sstate.server.address%/allarch/sstate-cache/PATH;downloadfilename=PATH \n \
                    file://.* %system.sstate.server.address%/other-sstate/sstate-cache/PATH;downloadfilename=PATH \n \
                    "' >> build/conf/local.conf
                echo 'BBPATH = "${'$'}{TOPDIR}"
                SRCDIR = "${'$'}{@os.path.abspath(os.path.join("${'$'}{TOPDIR}", "../src/"))}"
                
                BBLAYERS = " \
                  ${'$'}{SRCDIR}/meta-qt5 \
                  ${'$'}{SRCDIR}/oe-core/meta \
                  ${'$'}{SRCDIR}/meta-asteroid \
                  ${'$'}{SRCDIR}/meta-openembedded/meta-oe \
                  ${'$'}{SRCDIR}/meta-openembedded/meta-multimedia \
                  ${'$'}{SRCDIR}/meta-openembedded/meta-gnome \
                  ${'$'}{SRCDIR}/meta-openembedded/meta-networking \
                  ${'$'}{SRCDIR}/meta-smartphone/meta-android \
                  ${'$'}{SRCDIR}/meta-openembedded/meta-python \
                  ${'$'}{SRCDIR}/meta-openembedded/meta-filesystems \ ' > build/conf/bblayers.conf
                
                # Add the relevant hybris for real machine dependence
                if [ ${'$'}MACHINE != "qemux86" ]; then
                  echo '  ${'$'}{SRCDIR}/meta-smartwatch/meta-%system.MACHINE% \ ' >> build/conf/bblayers.conf
                fi
                
                echo '  "' >> build/conf/bblayers.conf
                
                # Try to initialize OE environment
                source ./src/oe-core/oe-init-build-env
            """.trimIndent()
        }
        script {
            name = "Build Package"
            id = "RUNNER_3"
            scriptContent = """
                source src/oe-core/oe-init-build-env > /dev/null
                bitbake \
                  --ui=teamcity \
                  %system.recipeName%
            """.trimIndent()
        }
        script {
            name = "Upload sstate-cache"
            id = "RUNNER_11"
            scriptContent = """
                Opts="-a --prune-empty-dirs --remove-source-files \ 
                    --checksum --progress"
                ServerAddr="%system.sstate.server.user%@%system.sstate.server.upload_address%:%system.sstate.server.location%"
                
                
                rsync ${'$'}{Opts} \
                    build/sstate-cache/fedora-35 ${'$'}{ServerAddr}/other-sstate/sstate-cache
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*::*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/other-sstate
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:%system.MACHINE%:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/%system.MACHINE%
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:%system.architecture%:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/%system.architecture%
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:allarch:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/all-arch
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_8"
            triggerRules = """
                +:root=${MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/recipes-asteroid-apps/%system.recipeName%/**
                +:root=${MetaAsteroid.id};comment=^\[%system.recipeName%\][:]:**
            """.trimIndent()

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
        }
        vcs {
            id = "TRIGGER_16"
            triggerRules = """+:root=${Packages_PackageSource.id};comment=^(?!\[NoBuild\]:).+:**"""

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
        }
        vcs {
            id = "TRIGGER_2"
            enabled = false
            triggerRules = """+:root=${MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:**"""

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
        }
    }

    features {
        sshAgent {
            id = "BUILD_EXT_13"
            teamcitySshKey = "Sstate Server Key"
        }
        pullRequests {
            id = "BUILD_EXT_11"
            vcsRootExtId = "${Packages_PackageSource.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
            }
        }
        commitStatusPublisher {
            id = "BUILD_EXT_12"
            enabled = false
            vcsRootExtId = "${Packages_PackageSource.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
            }
        }
        commitStatusPublisher {
            id = "BUILD_EXT_9"
            vcsRootExtId = "${MetaAsteroid.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
            }
        }
        pullRequests {
            id = "BUILD_EXT_8"
            vcsRootExtId = "${MetaAsteroid.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
            }
        }
    }
})
