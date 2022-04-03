package asteroid.devices

import asteroid.CoreVCS
import asteroid.Settings
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.PublishMode
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.sshExec
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

fun makeDeviceProject(device: String, architecture: String = "armv7vehf-neon"): Project {
    return Project {
        id("Devices_${device}")
        name = device
        buildType(BuildImage(device, architecture))
        buildType(BuildImageFromScratch(device, architecture))
    }
}

/**
 * Template for device image builder
 */
// TODO: Add Snapshot dependence
open class BuildImage(device: String, architecture: String) : BuildType({
    id("Devices_${device}_BuildImage")
    name = "Build Image"
    description = "Build Asteroid image with latest sstate-cache"

    enablePersonalBuilds = false
    artifactRules = """
        +:build/tmp-glibc/deploy/images/${device}/asteroid-image-${device}.ext4
        +:build/tmp-glibc/deploy/images/${device}/zImage-dtb-${device}.fastboot
    """.trimIndent()
    maxRunningBuilds = 1
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        CoreVCS.attachVCS(this, true)
    }

    steps {
        script {
            name = "Prepare config files"
            id = "RUNNER_2"
            scriptContent = """
                mkdir -p build/conf
                echo 'DISTRO = "asteroid"
                MACHINE = "$device"
                PACKAGE_CLASSES = "package_ipk"
                SSTATE_MIRRORS ?= " \
                    file://.* %system.sstate.server.address%/${device}/sstate-cache/PATH;downloadfilename=PATH \n \
                    file://.* %system.sstate.server.address%/${architecture}/sstate-cache/PATH;downloadfilename=PATH \n \
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
                  ${'$'}{SRCDIR}/meta-smartwatch/meta-${device} \
                  "' > build/conf/bblayers.conf
                
                # Try to initialize OE environment
                source ./src/oe-core/oe-init-build-env
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
        if (Settings.deploySstate) {
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
                    --include '*/' --include '*:*:*:*:*:${device}:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/${device}
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:${architecture}:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/${architecture}
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:allarch:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/all-arch
            """.trimIndent()
            }
        }
    }

    triggers {
        // TODO: Move MetaSmartwatch outside CoreVCS
        vcs {
            id = "TRIGGER_14"
            enabled = false
            triggerRules = """
                +:root=${CoreVCS.MetaSmartwatch.id};comment=^(?!\[NoBuild\]:).+:/meta-${device}/**
                +:root=${CoreVCS.MetaSmartwatch.id};comment=^(?:[^:\n]*)(${device})(?:[^:\n]*)[:]:**
                +:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:**
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
                +:root=${CoreVCS.MetaSmartwatch.id};comment=^(?!\[NoBuild\]:).+:/meta-${device}/**
                +:root=${CoreVCS.MetaSmartwatch.id};comment=^\[(?:[^\]\n]*)(${device})(?:[^\]\n]*)\][:]:**
                +:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:**
                -:root=${CoreVCS.MetaAsteroid.id}:/recipes-asteroid-apps/*
            """.trimIndent()

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
        }
    }

    features {
        if (Settings.deploySstate) {
            sshAgent {
                id = "BUILD_EXT_2"
                teamcitySshKey = "Sstate Server Key"
            }
        }
        if (Settings.pullRequests) {
            pullRequests {
                id = "BUILD_EXT_1"
                vcsRootExtId = "${CoreVCS.MetaSmartwatch.id}"
                provider = github {
                    authType = token {
                        token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                    }
                    filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
                }
            }
            pullRequests {
                id = "BUILD_EXT_7"
                vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
                provider = github {
                    authType = token {
                        token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                    }
                    filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
                }
            }
        }
        if (Settings.commitStatus) {
            commitStatusPublisher {
                id = "BUILD_EXT_4"
                enabled = false
                vcsRootExtId = "${CoreVCS.MetaSmartwatch.id}"
                publisher = github {
                    githubUrl = "https://api.github.com"
                    authType = personalToken {
                        token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                    }
                }
                param("github_oauth_user", Settings.commitUser)
            }
            commitStatusPublisher {
                id = "BUILD_EXT_5"
                vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
                publisher = github {
                    githubUrl = "https://api.github.com"
                    authType = personalToken {
                        token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                    }
                }
                param("github_oauth_user", Settings.commitUser)
            }
        }
    }
})

// TODO: Add Snapshot dependence
open class BuildImageFromScratch(device: String, architecture: String) : BuildType({
    id("Devices_${device}_BuildImageFromScratch")
    name = "Build Image (from scratch)"
    description = "Build Asteroid image and update the sstate-cache"

    enablePersonalBuilds = false
    artifactRules = "sstate-cache-${device}.tar.gz"
    maxRunningBuilds = 1
    publishArtifacts = PublishMode.SUCCESSFUL

    vcs {
        CoreVCS.attachVCS(this, true)
    }

    steps {
        script {
            name = "Prepare config files"
            id = "RUNNER_2"
            scriptContent = """
                mkdir -p build/conf
                echo 'DISTRO = "asteroid"
                MACHINE = "$device"
                PACKAGE_CLASSES = "package_ipk"
                SSTATE_MIRRORS ?= " \
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
                  ${'$'}{SRCDIR}/meta-smartwatch/meta-${device} \
                  "' > build/conf/bblayers.conf
                
                # Try to initialize OE environment
                source ./src/oe-core/oe-init-build-env
            """.trimIndent()
        }
        script {
            name = "Build Image"
            id = "RUNNER_3"
            scriptContent = """
                source src/oe-core/oe-init-build-env > /dev/null
                bitbake \
                  --ui=teamcity \
                  asteroid-image
            """.trimIndent()
        }
        if (Settings.deploySstate) {
            sshExec {
                name = "Delete old cache"
                id = "RUNNER_17"
                commands = "rm -r %system.sstate.server.location%/${device}"
                targetUrl = "%system.sstate.server.upload_address%"
                authMethod = sshAgent {
                    username = "%system.sstate.server.user%"
                }
                param("teamcitySshKey", "Sstate Server Key")
            }
            script {
                name = "Upload sstate-cache"
                id = "RUNNER_1"
                scriptContent = """
                ServerAddr="%system.sstate.server.user%@%system.sstate.server.upload_address%:/var/www/asteroidos"
                
                rsync ${'$'}{Opts} \
                    build/sstate-cache/fedora-35 ${'$'}{ServerAddr}/other-sstate/sstate-cache
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*::*' --exclude '*' \
                    build/sstate-cache ${'$'}{ServerAddr}/other-sstate
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:${device}:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/${device}
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:${architecture}:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/${architecture}
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:allarch:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/all-arch
            """.trimIndent()
            }
        }
        script {
            name = "Compress sstate-cache"
            id = "RUNNER_4"
            scriptContent = """
                cd build
                tar -cf ../sstate-cache-${device}.tar.gz sstate-cache
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_6"
            triggerRules = """
                +:root=${CoreVCS.MetaSmartwatch.id};comment=^\[Rebuild:(?:[^\]\n]*)(${device})(?:[^\]\n]*)\][:]:**
                +:root=${CoreVCS.MetaAsteroid.id};comment=^\[Rebuild:(?:[^\]\n]*)(${device})(?:[^\]\n]*)\][:]:**
            """.trimIndent()

            branchFilter = "+:<default>"
        }
        schedule {
            id = "TRIGGER_7"
            schedulingPolicy = weekly {
            }
            branchFilter = "+:<default>"
            triggerBuild = always()
            withPendingChangesOnly = false
        }
    }

    features {
        if (Settings.deploySstate) {
            sshAgent {
                id = "BUILD_EXT_6"
                teamcitySshKey = "Sstate Server Key"
            }
        }
    }
})