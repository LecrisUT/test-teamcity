package Devices.buildTypes

import Devices.vcsRoots.Devices_MetaSmatwtach
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.sshExec
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object Devices_BuildImageFromScratch : Template({
    name = "Build Image (from scratch)"
    description = "Build Asteroid image and update the sstate-cache"

    enablePersonalBuilds = false
    artifactRules = "sstate-cache-%system.MACHINE%.tar.gz"
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
                    file://.* https://asteroidos.lecris.dev//other-sstate/sstate-cache/PATH;downloadfilename=PATH \n \
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
            name = "Build Image"
            id = "RUNNER_3"
            scriptContent = """
                source src/oe-core/oe-init-build-env > /dev/null
                bitbake \
                  --ui=teamcity \
                  asteroid-image
            """.trimIndent()
        }
        sshExec {
            name = "Delete old cache"
            id = "RUNNER_17"
            enabled = false
            commands = "rm -r %system.sstate.server.location%/%system.MACHINE%"
            targetUrl = "%system.sstate.server.upload_address%"
            authMethod = sshAgent {
                username = "%system.sstate.server.user%"
            }
            param("teamcitySshKey", "Sstate Server Key")
        }
        script {
            name = "Upload sstate-cache"
            id = "RUNNER_1"
            enabled = false
            scriptContent = """
                alias rsync2 = "rsync -a --prune-empty-dirs --remove-source-files \ 
                    --checksum --progress"
                alias rsync3 = "rsync2 \
                    --include '*/'  --exclude '*'"
                ServerAddr="%system.sstate.server.user%@%system.sstate.server.upload_address%:/var/www/asteroidos"
                
                
                rsync2 build/sstae-cache/fedora-35 ${'$'}{ServerAddr}/other-sstate/sstate-cache
                rsync3 --include '*::*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/other-sstate
                rsync3 --include '*:%system.MACHINE%:*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/other-sstate
                rsync3 --include '*:%system.architecture%:*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/other-sstate
                rsync3 --include '*:allarch:*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/other-sstate
            """.trimIndent()
        }
        script {
            name = "Compress sstate-cache"
            id = "RUNNER_4"
            scriptContent = """
                cd build
                tar -cf ../sstate-cache-%system.MACHINE%.tar.gz sstate-cache
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_6"
            triggerRules = """
                +:root=${Devices_MetaSmatwtach.id};comment=^\[Rebuild:(?:[^\]\n]*)(%system.MACHINE%)(?:[^\]\n]*)\][:]:**
                +:root=Asteroid_MetaAsteroid:comment=^\[Rebuild:(?:[^\]\n]*)(%system.MACHINE%)(?:[^\]\n]*)\][:]:**
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
        sshAgent {
            id = "BUILD_EXT_6"
            teamcitySshKey = "Sstate Server Key"
        }
    }
})
