package asteroid.packages

import asteroid.*
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

class PackageProject(val recipe: String, recipeVCSroot: VcsRoot? = null) : Project({
	id("Packages_AsteroidApps_${recipe.filter { it.isLetterOrDigit() }}")
	name = recipe
}) {
	val recipeVCS = recipeVCSroot ?: GitVcsRoot {
		id("Packages_AsteroidApps_${this@PackageProject.recipe.filter { it.isLetterOrDigit() }}VCS")
		name = "${this@PackageProject.recipe} Source"
		url = "https://github.com/${Settings.fork}/${this@PackageProject.recipe}.git"
		branch = "refs/heads/master"
	}
	val buildPackage = BuildPackage(recipe, recipeVCS)

	init {
		vcsRoot(recipeVCS)
		buildType(buildPackage)
	}
}

open class BuildPackage(recipe: String, recipeVCS: VcsRoot, coreApp: Boolean = true) : BuildType({
	id("Packages_AsteroidApps_${recipe.filter { it.isLetterOrDigit() }}_BuildPackage")
	name = "Build Package"
	description = "Build a specific recipe"

	vcs {
		CoreVCS.attachVCS(this)
		root(recipeVCS, "+:.=>src/${recipe}")
	}

	steps {
		script {
			initScript(this)
		}
		script {
			name = "Build Package"
			bitbakeBuild(this, recipe)
		}
		if (Settings.deploySstate) {
			script {
				updateSstate(this)
			}
		}
	}

	triggers {
		vcs {
			val coreAppTrigger = if (coreApp) """
				+:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/recipes-asteroid/${recipe}/**
				+:root=${CoreVCS.MetaAsteroid.id};comment=^\[${recipe}\][:]:**
			""".trimStart().trimEnd() else ""
			triggerRules = """
				+:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:**
				-:root=${CoreVCS.MetaAsteroid.id}:/recipes-asteroid/**
				$coreAppTrigger
				+:root=${recipeVCS.id};comment=^(?!\[NoBuild\]:).+:**
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
				teamcitySshKey = "Sstate Server Key"
			}
		}
		if (Settings.pullRequests) {
			pullRequests {
				vcsRootExtId = "${recipeVCS.id}"
				provider = github {
					authType = token {
						token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
					}
					filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
				}
			}
			pullRequests {
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
				vcsRootExtId = "${recipeVCS.id}"
				publisher = github {
					githubUrl = "https://api.github.com"
					authType = personalToken {
						token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
					}
				}
				param("github_oauth_user", Settings.commitUser)
			}
			if (coreApp) {
				commitStatusPublisher {
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
	}
})