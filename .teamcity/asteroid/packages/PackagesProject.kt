package asteroid.packages

import asteroid.CoreVCS
import asteroid.Settings
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

object PackagesProject : Project({
	id("Packages")
	name = "Packages"
	description = "Package projects"

	subProject(AsteroidAppsProject)

	buildType(BuildAll)

	sequential {
		parallel {
			for (pkg in AsteroidAppsProject.packages)
				buildType(pkg.buildPackage)
		}
		buildType(BuildAll){
			onDependencyFailure = FailureAction.CANCEL
		}
	}
})

object BuildAll : BuildType({
	id("Packages_BuildAll")
	name = "Build all Packages"
	description = "Build all packages"

	vcs {
		root(CoreVCS.Asteroid)
		root(CoreVCS.MetaAsteroid)
		root(CoreVCS.TempRepository)
	}

	triggers {
		vcs {
			// TODO: Add quiet period
			watchChangesInDependencies = true
			triggerRules = """
				+:.
                +:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/**
                -:root=${CoreVCS.MetaAsteroid.id}:/recipes-asteroid-apps/*
				+:root=${CoreVCS.Asteroid.id}:/.teamcity/*
				-:root=${CoreVCS.Asteroid.id}:/.teamcity/*/**
				+:root=${CoreVCS.Asteroid.id}:/.teamcity/packages/**
				+:root=${CoreVCS.TempRepository.id}:/.teamcity/*
				-:root=${CoreVCS.TempRepository.id}:/.teamcity/*/**
				+:root=${CoreVCS.TempRepository.id}:/.teamcity/packages/**
            """.trimIndent()

			branchFilter = """
                +:<default>
                +:pull/*
            """.trimIndent()
		}
	}
	features {
		if (Settings.pullRequests) {
			pullRequests {
				vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
				provider = github {
					authType = token {
						token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
					}
					filterAuthorRole = PullRequests.GitHubRoleFilter.MEMBER_OR_COLLABORATOR
				}
			}
			pullRequests {
				vcsRootExtId = "${CoreVCS.Asteroid.id}"
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
				vcsRootExtId = "${CoreVCS.MetaAsteroid.id}"
				publisher = github {
					githubUrl = "https://api.github.com"
					authType = personalToken {
						token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
					}
				}
				param("github_oauth_user", Settings.commitUser)
			}
			commitStatusPublisher {
				vcsRootExtId = "${CoreVCS.Asteroid.id}"
				publisher = github {
					githubUrl = "https://api.github.com"
					authType = personalToken {
						token = "credentialsJSON:0b803d82-f0a8-42ee-b8f9-0fca109a14ab"
					}
				}
				param("github_oauth_user", Settings.commitUser)
			}
			commitStatusPublisher {
				vcsRootExtId = "${CoreVCS.TempRepository.id}"
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