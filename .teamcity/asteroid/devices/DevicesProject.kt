package asteroid.devices

import asteroid.*
import asteroid.CoreVCS.GitAPIChecker
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.FailureAction
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.schedule
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

// TODO: Make a separate TeamCity vcsSettings in MetaSmartwatch
object DevicesProject : Project({
	id("Devices")
	name = "Devices"
	description = "Device projects"

	// Create the subProjects
	// TODO: Make each device project external
	// TODO: Add a map of device -> architecture
	val devices = Settings.devices.map { DeviceProject(it) }
	for (device in devices)
		subProject(device)
	// TODO: Link to root Asteroid Project for external dependence

	// TODO: Uncomment when have common base
//	// Attach MetaSmartwatch vcs
//	vcsRoot(CoreVCS.MetaSmartwatch)

	if (Settings.withSstate) {
		buildType(BuildBase)
		buildType(BuildAll)
	}
	if (Settings.cleanBuilds) {
		buildType(BuildBaseFromScratch)
		buildType(BuildAllFromScratch)
	}

	if (Settings.withSstate) {
		sequential {
			buildType(BuildBase)
			parallel {
				for (device in devices)
					buildType(device.buildImage) {
						onDependencyFailure = FailureAction.CANCEL
					}
			}
			buildType(BuildAll) {
				onDependencyFailure = FailureAction.CANCEL
			}
		}
	}
	if (Settings.cleanBuilds) {
		sequential {
			buildType(BuildBaseFromScratch)
			parallel {
				for (device in devices)
					buildType(device.buildImageFromScratch) {
						onDependencyFailure = FailureAction.CANCEL
					}
			}
			buildType(BuildAllFromScratch) {
				onDependencyFailure = FailureAction.CANCEL
			}
		}
	}
	params {
		text(
			"system.image.dev-suffix",
			"",
			"Development suffix",
			"Recipe suffix to build development or debug version",
			readOnly = false, allowEmpty = true
		)
	}
})

object BuildBase : BuildType({
	// TODO: Change to generic device
	id("Devices_BuildBase")
	name = "Build device base"
	description = "Build a prototype device with sstate-server"

	vcs {
		CoreVCS.attachVCS(this, true)
	}

	steps {
		script {
			initScript(this)
		}
		script {
			name = "Build Image"
			bitbakeBuild(this)
		}
		if (Settings.deploySstate) {
			script {
				updateSstate(this, "sturgeon", "armv7vehf-neon")
			}
		}
	}

	features {
		if (Settings.deploySstate) {
			sshAgent {
				teamcitySshKey = "Sstate Server Key"
			}
		}
	}
})

object BuildBaseFromScratch : BuildType({
	// TODO: Change to generic device
	id("Devices_BuildBaseFromScratch")
	name = "Build device base (from scratch)"
	description = "Build a prototype device with clean environment"

	vcs {
		CoreVCS.attachVCS(this, true)
	}

	steps {
		script {
			initScript(this, false)
		}
		script {
			name = "Build Image"
			bitbakeBuild(this)
		}
		if (Settings.deploySstate) {
			script {
				updateSstate(this, true)
			}
		}
	}

	features {
		if (Settings.deploySstate) {
			sshAgent {
				teamcitySshKey = "Sstate Server Key"
			}
		}
	}
})

object BuildAll : BuildType({
	id("Devices_BuildAll")
	name = "Build all devices"
	description = "Build Asteroid image for all devices with latest sstate-cache"

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
				+:/**
				-:root=${CoreVCS.MetaSmartwatch.id}:/**
				+:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/**
				-:root=${CoreVCS.MetaAsteroid.id}:/recipes-asteroid-apps/*
			""".trimIndent()

			branchFilter = """
				+:<default>
				+:pull/*
			""".trimIndent()
		}
		vcs {
			triggerRules = """
				+:root=${CoreVCS.Asteroid.id}:/.teamcity/*
				-:root=${CoreVCS.Asteroid.id}:/.teamcity/*/**
				+:root=${CoreVCS.Asteroid.id}:/.teamcity/devices/**
				+:root=${CoreVCS.TempRepository.id}:/.teamcity/*
				-:root=${CoreVCS.TempRepository.id}:/.teamcity/*/**
				+:root=${CoreVCS.TempRepository.id}:/.teamcity/devices/**
			""".trimIndent()

			branchFilter = """
				+:<default>
			""".trimIndent()
		}
	}
	features {
		var gitChecker: GitAPIChecker?
		if (Settings.pullRequests) {
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
			gitChecker = GitAPIChecker.Create(CoreVCS.Asteroid.url!!, Settings.GithubTokenID)
			if (gitChecker?.checkPR() == true)
				pullRequests {
					vcsRootExtId = "${CoreVCS.Asteroid.id}"
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
			gitChecker = GitAPIChecker.Create(CoreVCS.Asteroid.url!!, Settings.GithubTokenID)
			if (gitChecker?.checkCommitStatus() == true)
				commitStatusPublisher {
					vcsRootExtId = "${CoreVCS.Asteroid.id}"
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
			gitChecker = GitAPIChecker.Create(CoreVCS.TempRepository.url!!, Settings.GithubTokenID)
			if (gitChecker?.checkCommitStatus() == true)
				commitStatusPublisher {
					vcsRootExtId = "${CoreVCS.TempRepository.id}"
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
})

object BuildAllFromScratch : BuildType({
	id("Devices_BuildAllFromScratch")
	name = "Build all devices (From scratch)"
	description = "Build Asteroid image for all devices"

	triggers {
		schedule {
			schedulingPolicy = weekly {
			}
			branchFilter = "+:<default>"
			triggerBuild = always()
			withPendingChangesOnly = false
		}
	}
})