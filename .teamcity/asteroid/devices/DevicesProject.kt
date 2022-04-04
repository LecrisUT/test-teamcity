package asteroid.devices

import asteroid.CoreVCS
import asteroid.Settings
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
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
	// TODO: Move MetaSmartwatch outside CoreVCS
	vcsRoot(CoreVCS.MetaSmartwatch)

	buildType(BuildAll)
	if (Settings.cleanBuilds)
		buildType(BuildAllFromScratch)

	sequential {
		parallel {
			for (device in devices)
				buildType(device.buildImage)
		}
		buildType(BuildAll)
	}
	if (Settings.cleanBuilds)
		sequential {
			parallel {
				for (device in devices)
					buildType(device.buildImageFromScratch)
			}
			buildType(BuildAllFromScratch)
		}
})

object BuildAll : BuildType({
	id("Devices_BuildAll")
	name = "Build all devices"
	description = "Build Asteroid image for all devices with latest sstate-cache"

	triggers {
		vcs {
			triggerRules = """
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