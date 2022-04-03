package asteroid.packages.asteroidApps

import asteroid.CoreVCS
import asteroid.Settings
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

fun makePackageProject(recipe: String, recipeVCSroot: VcsRoot? = null): Project {
    return Project {
        id("Packages_AsteroidApps_${recipe.filter { it.isLetterOrDigit() }}")
        name = recipe

        val recipeVCS = recipeVCSroot ?: GitVcsRoot {
            id("Packages_AsteroidApps_${recipe.filter { it.isLetterOrDigit() }}VCS")
            name = "$recipe Source"
            url = "https://github.com/${DslContext.getParameter("Fork")}/${recipe}.git"
            branch = "refs/heads/master"
        }

        vcsRoot(recipeVCS)

        buildType(BuildPackage(recipe, recipeVCS))
    }
}


open class BuildPackage(recipe: String, recipeVCS: VcsRoot) : BuildType({
    id("Packages_AsteroidApps_${recipe.filter { it.isLetterOrDigit() }}_BuildPackage")
    name = "Build Package"
    description = "Build a specific recipe"

    enablePersonalBuilds = false
    maxRunningBuilds = 1

    vcs {
        CoreVCS.attachVCS(this)
        root(recipeVCS, "+:.=>src/${recipe}")
    }

    // TODO: Change the hardcoded sturgeon to generic
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
                    file://.* %system.sstate.server.address%/sturgeon/sstate-cache/PATH;downloadfilename=PATH \n \
                    file://.* %system.sstate.server.address%/armv7vehf-neon/sstate-cache/PATH;downloadfilename=PATH \n \
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
        if (Settings.deploySstate) {
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
                    --include '*/' --include '*:*:*:*:*:sturgeon:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/sturgeon
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:armv7vehf-neon:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/armv7vehf-neon
                rsync ${'$'}{Opts} \
                    --include '*/' --include '*:*:*:*:*:allarch:*' --exclude '*' \ 
                    build/sstate-cache ${'$'}{ServerAddr}/all-arch
            """.trimIndent()
            }
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_8"
            triggerRules = """
                +:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/recipes-asteroid/%system.recipeName%/**
                +:root=${CoreVCS.MetaAsteroid.id};comment=^\[%system.recipeName%\][:]:**
                +:root=${recipeVCS.id};comment=^(?!\[NoBuild\]:).+:**
            """.trimIndent()

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
        if (Settings.deploySstate) {
            sshAgent {
                id = "BUILD_EXT_13"
                teamcitySshKey = "Sstate Server Key"
            }
        }
        if (Settings.pullRequests) {
            pullRequests {
                id = "BUILD_EXT_11"
                vcsRootExtId = "${recipeVCS.id}"
                provider = github {
                    authType = token {
                        token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
                    }
                    filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
                }
            }
            pullRequests {
                id = "BUILD_EXT_8"
                vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
                provider = github {
                    authType = token {
                        token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
                    }
                    filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
                }
            }
        }
        if (Settings.commitStatus) {
            commitStatusPublisher {
                id = "BUILD_EXT_12"
                enabled = false
                vcsRootExtId = "${recipeVCS.id}"
                publisher = github {
                    githubUrl = "https://api.github.com"
                    authType = personalToken {
                        token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
                    }
                }
                param("github_oauth_user", Settings.commitUser)
            }
            commitStatusPublisher {
                id = "BUILD_EXT_9"
                vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
                publisher = github {
                    githubUrl = "https://api.github.com"
                    authType = personalToken {
                        token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
                    }
                }
                param("github_oauth_user", Settings.commitUser)
            }
        }
    }
})