package Devices.buildTypes

import Devices.vcsRoots.Devices_MetaSmatwtach
import _Self.vcsRoots.MetaAsteroid
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.SSHUpload
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.sshUpload
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object Devices_BuildImage : Template({
    name = "Build Image"
    description = "Build Asteroid image with latest sstate-cache"

    enablePersonalBuilds = false
    artifactRules = """
        +:build/tmp-glibc/deploy/images/%system.MACHINE%/asteroid-image-%system.MACHINE%.ext4
        +:build/tmp-glibc/deploy/images/%system.MACHINE%/zImage-dtb-%system.MACHINE%.fastboot
    """.trimIndent()
    maxRunningBuilds = 1
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        root(_Self.vcsRoots.OpenEmbeddedCore, "+:.=>src/oe-core")
        root(_Self.vcsRoots.Bitbake, "+:.=>src/oe-core/bitbake")
        root(_Self.vcsRoots.MetaOpenEmbedded, "+:.=>src/meta-openembedded")
        root(Devices.vcsRoots.Devices_MetaSmatwtach, "+:.=>src/meta-smartwatch")
        root(_Self.vcsRoots.MetaQt5, "+:.=>src/meta-qt5")
        root(_Self.vcsRoots.MetaSmartphone, "+:.=>src/meta-smartphone")
        root(_Self.vcsRoots.MetaAsteroid, "+:.=>src/meta-asteroid")

        cleanCheckout = true
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
                  ${'$'}{SRCDIR}/meta-openembedded/meta-filesystems \
                  ${'$'}{SRCDIR}/meta-smartwatch/meta-%system.MACHINE% \
                  "' > build/conf/bblayers.conf
                
                # Try to initialize OE environment
                source ./src/oe-core/oe-init-build-env
            """.trimIndent()
        }
        script {
            name = "Debug filestructure"
            id = "RUNNER_7"
            enabled = false
            scriptContent = """
                cd build
                tree
                tail -n +0 conf/*
            """.trimIndent()
        }
        script {
            name = "Build Image"
            id = "RUNNER_3"
            scriptContent = """
                source src/oe-core/oe-init-build-env > /dev/null
                #bitbake \
                #  --ui=teamcity \
                #  asteroid-image
                
                echo "Starting bitbake"
                
                bitbake --ui=teamcity asteroid-image
            """.trimIndent()
        }
        sshUpload {
            id = "RUNNER_16"
            enabled = false
            transportProtocol = SSHUpload.TransportProtocol.SCP
            sourcePath = """
                build/sstate-cache/fedora-35 => other-sstate/sstate-cache/fedora-35
                build/sstate-cache/**/*:*:*:*:*::* => other-sstate/sstate-cache
                build/sstate-cache/**/*:*:*:*:*:%system.MACHINE%:* => %system.MACHINE%/sstate-cache
                build/sstate-cache/**/*:*:*:*:*:%system.architecture%:* => %system.architecture%/sstate-cache
                build/sstate-cache/**/*:*:*:*:*:allarch:* => allarch/sstate-cache
                build/sstate-cache => sstate-cache
            """.trimIndent()
            targetUrl = "%system.sstate.server.upload_address%:%system.sstate.server.location%"
            authMethod = sshAgent {
                username = "%system.sstate.server.user%"
            }
        }
        script {
            name = "Upload sstate-cache"
            id = "RUNNER_21"
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
        schedule {
            id = "TRIGGER_5"
            enabled = false
            triggerRules = "+:**"
            triggerBuild = always()
        }
        vcs {
            id = "TRIGGER_14"
            enabled = false
            triggerRules = """
                +:root=${Devices_MetaSmatwtach.id};comment=^(?!\[NoBuild\]:).+:/meta-%system.MACHINE%/**
                +:root=${Devices_MetaSmatwtach.id};comment=^(?:[^:\n]*)(%system.MACHINE%)(?:[^:\n]*)[:]:**
                +:root=${MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:**
            """.trimIndent()

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
            enableQueueOptimization = false
        }
        vcs {
            id = "TRIGGER_15"
            triggerRules = """
                +:root=${Devices_MetaSmatwtach.id};comment=^(?!\[NoBuild\]:).+:/meta-%system.MACHINE%/**
                +:root=${Devices_MetaSmatwtach.id};comment=^\[(?:[^\]\n]*)(%system.MACHINE%)(?:[^\]\n]*)\][:]:**
                +:root=${MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:**
                -:root=${MetaAsteroid.id}:/recipes-asteroid-apps/*
            """.trimIndent()

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
        }
    }

    features {
        sshAgent {
            id = "BUILD_EXT_2"
            teamcitySshKey = "Sstate Server Key"
        }
        pullRequests {
            id = "BUILD_EXT_1"
            vcsRootExtId = "${Devices_MetaSmatwtach.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.EVERYBODY
            }
        }
        commitStatusPublisher {
            id = "BUILD_EXT_4"
            enabled = false
            vcsRootExtId = "${Devices_MetaSmatwtach.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
            }
            param("github_oauth_user", "LecrisUT")
        }
        pullRequests {
            id = "BUILD_EXT_7"
            vcsRootExtId = "${MetaAsteroid.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
            }
        }
        commitStatusPublisher {
            id = "BUILD_EXT_5"
            vcsRootExtId = "${MetaAsteroid.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
            }
            param("github_oauth_user", "LecrisUT")
        }
    }
})
