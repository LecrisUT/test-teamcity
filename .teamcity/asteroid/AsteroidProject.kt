package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.ErrorConsumer
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.activeStorage

object AsteroidProject : Project({
	description = "Base Asteroid Project"

	// Attach vcsRoots from CoreVCS
	for (vcs in CoreVCS.all())
		if (vcs != CoreVCS.MetaSmartwatch)
			vcsRoot(vcs)

	// Attach InitWorkspace build
	buildType(InitWorkspace)

	// Attach the subProjects
	// TODO: Link to subprojects externally
	subProjects(
		asteroid.devices.DevicesProject,
		asteroid.packages.PackagesProject,
		asteroid.thirdparty.ThirdPartyProject
	)

	// Define the build parameters inherited by the subprojects
	params {
		text(
			"system.sstate.server.address",
			Settings.sstateServer.url,
			label = "SState server public address",
			description = "Public address serving sstate-cache",
			readOnly = true,
			allowEmpty = false
		)
		if (Settings.deploySstate) {
			text(
				"system.sstate.server.location",
				Settings.sstateServer.location ?: "",
				label = "Sstate server location",
				description = "Path location of the sstate-cache",
				readOnly = true, allowEmpty = true
			)
			text(
				"system.sstate.server.user",
				Settings.sstateServer.user ?: "",
				label = "SState server user",
				description = "Username used to upload sstate-cache",
				readOnly = true, allowEmpty = true
			)
			text(
				"system.sstate.server.upload_address",
				Settings.sstateServer.backendUrl ?: "",
				label = "SState server backend address",
				description = "Backend address to upload the sstate-cache",
				readOnly = true, allowEmpty = false
			)
		}
	}

	features {
		activeStorage {
			activeStorageID = "DefaultStorage"
		}
	}
}) {
	override fun validate(consumer: ErrorConsumer) {
		super.validate(consumer)
		if (Settings.deploySstate) {
			// TODO: check that SSH key is present
			//  Currently this feature is unavailable
		}
		if (Settings.pullRequests || Settings.commitStatus) {
			// TODO: check that SSH key and and OAuth token are present
		}
	}
}
