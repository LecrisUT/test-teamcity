package asteroid.packages

import asteroid.*
import asteroid.CoreVCS.GitAPIChecker
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

class PackageProject(val pkg: String) : Project({
	id("Packages_AsteroidApps_${pkg.filter { it.isLetterOrDigit() }}")
	name = pkg
}) {

	val recipeVCS: GitVcsRoot
	val buildPackage: BuildPackage
	val recipe: String

	init {
		val json = Settings.overrides?.optJSONObject("packages")?.optJSONObject(pkg)
		val gitName = json?.optString("gitName") ?: pkg
		recipe = json?.optString("recipe") ?: pkg
		recipeVCS = CoreVCS.GitVcsRoot_fallback {
			id("Packages_AsteroidApps_${this@PackageProject.pkg.filter { it.isLetterOrDigit() }}VCS")
			name = "${this@PackageProject.pkg} Source"
			gitBase = "https://github.com/"
			url = "${Settings.fork}/$gitName.git"
			fallback_url = "${Settings.upstream}/$gitName.git"
			branch = "refs/heads/master"
		}
		buildPackage = BuildPackage(pkg, recipeVCS, recipe)
		buildPackage.vcs.root(recipeVCS, "+:.=>src/$gitName")
		vcsRoot(recipeVCS)
		buildType(buildPackage)
	}
}

open class BuildPackage(pkg: String, recipeVCS: GitVcsRoot, recipe: String = pkg, coreApp: Boolean = true) : BuildType({
	id("Packages_AsteroidApps_${recipe.filter { it.isLetterOrDigit() }}_BuildPackage")
	name = "Build Package"
	description = "Build a specific recipe"

	vcs {
		CoreVCS.attachVCS(this, true)
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
				+:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/recipes-asteroid/$pkg/**
				+:root=${CoreVCS.MetaAsteroid.id};comment=^\[$pkg\][:]:**
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
		var gitChecker: GitAPIChecker?
		if (Settings.pullRequests) {
			gitChecker = GitAPIChecker.Create(recipeVCS.url!!, Settings.GithubTokenID)
			if (gitChecker?.checkPR() == true)
				pullRequests {
					vcsRootExtId = "${recipeVCS.id}"
					when (gitChecker!!.hubType) {
						CoreVCS.GitRepoHubType.Github -> {
							provider = github {
								authType = token {
									token = Settings.GithubTokenID
								}
								filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
							}
						}
					}
				}
			gitChecker = GitAPIChecker.Create(CoreVCS.MetaAsteroid.url!!, Settings.GithubTokenID)
			if (gitChecker?.checkPR() == true)
				pullRequests {
					vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
					when (gitChecker!!.hubType) {
						CoreVCS.GitRepoHubType.Github -> {
							provider = github {
								authType = token {
									token = Settings.GithubTokenID
								}
								filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
							}
						}
					}
				}
		}
		if (Settings.commitStatus) {
			gitChecker = GitAPIChecker.Create(recipeVCS.url!!, Settings.GithubTokenID)
			if (gitChecker?.checkCommitStatus() == true)
				commitStatusPublisher {
					vcsRootExtId = "${recipeVCS.id}"
					when (gitChecker!!.hubType) {
						CoreVCS.GitRepoHubType.Github -> {
							publisher = github {
								githubUrl = "https://api.github.com"
								authType = personalToken {
									token = Settings.GithubTokenID
								}
							}
							param("github_oauth_user", gitChecker!!.commitUser)
						}
					}
				}
			if (coreApp) {
				gitChecker = GitAPIChecker.Create(CoreVCS.MetaAsteroid.url!!, Settings.GithubTokenID)
				if (gitChecker?.checkCommitStatus() == true)
					commitStatusPublisher {
						vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
						when (gitChecker!!.hubType) {
							CoreVCS.GitRepoHubType.Github -> {
								publisher = github {
									githubUrl = "https://api.github.com"
									authType = personalToken {
										token = Settings.GithubTokenID
									}
								}
								param("github_oauth_user", gitChecker!!.commitUser)
							}
						}
					}
			}
		}
	}
})