package asteroid.devices

import asteroid.*
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.ErrorConsumer
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.PublishMode
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.commitStatusPublisher
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.pullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.sshAgent
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

class DeviceProject(device: String, architecture: String = "armv7vehf-neon") : Project({
	id("Devices_${device}")
	name = device
}) {
	val buildImage = BuildImage(device, architecture)
	val buildImageFromScratch = BuildImageFromScratch(device, architecture)

	init {
		buildType(buildImage)
		if (Settings.cleanBuilds)
			buildType(buildImageFromScratch)
	}
}

/**
 * Template for device image builder
 */
// TODO: Add Snapshot dependence
open class BuildImage(device: String, architecture: String) : BuildType({
	id("Devices_${device}_BuildImage")
	name = "Build Image"
	description = "Build Asteroid image for $device with latest sstate-cache"

	artifactRules = """
        +:build/tmp-glibc/deploy/images/${device}/asteroid-image-${device}.ext4
        +:build/tmp-glibc/deploy/images/${device}/zImage-dtb-${device}.fastboot
    """.trimIndent()
	publishArtifacts = PublishMode.SUCCESSFUL

	vcs {
		CoreVCS.attachVCS(this, true)
	}

	steps {
		script {
			initScript(this, device, architecture)
		}
		script {
			name = "Build Image"
			bitbakeBuild(this)
		}
		if (Settings.deploySstate) {
			script {
				updateSstate(this, device, architecture)
			}
		}
	}

	triggers {
		// TODO: Move MetaSmartwatch outside CoreVCS
		vcs {
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
				teamcitySshKey = "Sstate Server Key"
			}
		}
		if (Settings.pullRequests) {
			pullRequests {
				vcsRootExtId = "${CoreVCS.MetaSmartwatch.id}"
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
				vcsRootExtId = "${CoreVCS.MetaSmartwatch.id}"
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
}) {
	override fun validate(consumer: ErrorConsumer) {
		super.validate(consumer)
		// TODO: Validate that Github token works
	}
}

// TODO: Add Snapshot dependence
open class BuildImageFromScratch(device: String, architecture: String) : BuildType({
	id("Devices_${device}_BuildImageFromScratch")
	name = "Build Image (from scratch)"
	description = "Build Asteroid image for $device from scratch and update the sstate-cache"

	artifactRules = "sstate-cache-${device}.tar.gz"
	maxRunningBuilds = 1
	publishArtifacts = PublishMode.SUCCESSFUL

	vcs {
		CoreVCS.attachVCS(this, true)
	}

	steps {
		script {
			initScript(this, device, architecture, false)
		}
		script {
			bitbakeBuild(this)
		}
		if (Settings.deploySstate) {
			script {
				updateSstate(this, device, architecture, true)
			}
		}
		script {
			name = "Compress sstate-cache"
			scriptContent = """
                tar -cf sstate-cache-${device}.tar.gz build/sstate-cache
            """.trimIndent()
		}
	}

	triggers {
		vcs {
			triggerRules = """
                +:root=${CoreVCS.MetaSmartwatch.id};comment=^\[Rebuild:(?:[^\]\n]*)(${device})(?:[^\]\n]*)\][:]:**
                +:root=${CoreVCS.MetaAsteroid.id};comment=^\[Rebuild:(?:[^\]\n]*)(${device})(?:[^\]\n]*)\][:]:**
            """.trimIndent()

			branchFilter = "+:<default>"
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