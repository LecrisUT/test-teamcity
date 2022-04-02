package asteroid.packages.asteroidApps

import asteroid.InitWorkspace
import asteroid.CoreVCS
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

fun makePackageProject(recipe: String, recipeVCSroot: VcsRoot? = null): Project {
    return Project {
        id("asteroidApps/${recipe}")
        name = recipe

        val recipeVCS = recipeVCSroot ?: GitVcsRoot {
            id("asteroidApps/${recipe}VCS")
            name = "$recipe Source"
            url = "git@github.com:${DslContext.getParameter("Fork")}/${recipe}.git"
            branch = "refs/heads/master"
            authMethod = uploadedKey {
                uploadedKey = "Github Deploy Key"
            }
        }

        vcsRoot(recipeVCS)

        buildType(BuildPackage(recipe, recipeVCS))
    }
}


open class BuildPackage(recipe: String, recipeVCS: VcsRoot) : BuildType({
    name = "Build Package"
    description = "Build a specific recipe"

    enablePersonalBuilds = false
    maxRunningBuilds = 1

    vcs {
        InitWorkspace.vcs
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
                  ${recipe}
            """.trimIndent()
        }
        script {
            name = "Upload sstate-cache"
            id = "RUNNER_10"
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
                +:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/recipes-asteroid/%system.recipeName%/**
                +:root=${CoreVCS.MetaAsteroid.id};comment=^\[%system.recipeName%\][:]:**
            """.trimIndent()

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
        }
        vcs {
            id = "TRIGGER_16"
            triggerRules = """+:root=${recipeVCS.id};comment=^(?!\[NoBuild\]:).+:**"""

            branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
        }
        vcs {
            id = "TRIGGER_2"
            enabled = false
            triggerRules = """+:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:**"""

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
            vcsRootExtId = "${recipeVCS.id}"
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
            vcsRootExtId = "${recipeVCS.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
            }
        }
        pullRequests {
            id = "BUILD_EXT_8"
            vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
            provider = github {
                authType = token {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
                filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
            }
        }
        commitStatusPublisher {
            id = "BUILD_EXT_9"
            vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
            publisher = github {
                githubUrl = "https://api.github.com"
                authType = personalToken {
                    token = "credentialsJSON:ff37fd15-101a-4141-b93e-7d76761e3b8a"
                }
            }
        }
    }
})