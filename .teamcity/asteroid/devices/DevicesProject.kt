package asteroid.devices

import asteroid.*
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

	// Attach MetaSmartwatch vcs
	vcsRoot(CoreVCS.MetaSmartwatch)

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
					buildType(device.buildImage){
						onDependencyFailure = FailureAction.CANCEL
					}
			}
			buildType(BuildAll){
				onDependencyFailure = FailureAction.CANCEL
			}
		}
	}
	if (Settings.cleanBuilds) {
		sequential {
			buildType(BuildBaseFromScratch)
			parallel {
				for (device in devices)
					buildType(device.buildImageFromScratch){
						onDependencyFailure = FailureAction.CANCEL
					}
			}
			buildType(BuildAllFromScratch){
				onDependencyFailure = FailureAction.CANCEL
			}
		}
	}
})

object BuildBase : BuildType({
	// TODO: Change to generic device
	id("Devices_BuildBase")
	name = "Build device base"
	description = "Build a prototype device with sstate-server"

	vcs {
		CoreVCS.attachVCS(this)
	}

	steps {
		script {
			initScript(this, "sturgeon", "armv7vehf-neon")
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
		CoreVCS.attachVCS(this,true)
	}

	steps {
		script {
			initScript(this, "sturgeon", "armv7vehf-neon",false)
		}
		script {
			name = "Build Image"
			bitbakeBuild(this)
		}
		if (Settings.deploySstate) {
			script {
				updateSstate(this,true)
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
				+:.
                +:root=${CoreVCS.MetaAsteroid.id};comment=^(?!\[NoBuild\]:).+:/**
                -:root=${CoreVCS.MetaAsteroid.id}:/recipes-asteroid-apps/*
				+:root=${CoreVCS.Asteroid.id}:/.teamcity/*
				-:root=${CoreVCS.Asteroid.id}:/.teamcity/*/**
				+:root=${CoreVCS.Asteroid.id}:/.teamcity/devices/**
				+:root=${CoreVCS.TempRepository.id}:/.teamcity/*
				-:root=${CoreVCS.TempRepository.id}:/.teamcity/*/**
				+:root=${CoreVCS.TempRepository.id}:/.teamcity/devices/**
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