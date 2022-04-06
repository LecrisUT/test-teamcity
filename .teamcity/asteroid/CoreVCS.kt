package asteroid

import jetbrains.buildServer.configs.kotlin.v2019_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsSettings
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot
import java.net.HttpURLConnection
import java.net.URL


object CoreVCS {
	var Asteroid = GitVcsRoot_fallback {
		id("AsteroidVCS")
		name = "Asteroid"
		gitBase = "https://github.com/"
		url = "${Settings.fork}/asteroid.git"
		fallback_url = "${Settings.upstream}/asteroid.git"
		branch = "refs/heads/master"
	}
	var OpenEmbeddedCore = GitVcsRoot {
		id("OpenEmbeddedVCS")
		name = "OpenEmbedded Core"
		url = "https://github.com/openembedded/openembedded-core.git"
		branch = "refs/heads/honister"
	}
	var Bitbake = GitVcsRoot {
		id("BitBakeVCS")
		name = "Bitbake"
		url = "https://github.com/openembedded/bitbake.git"
		branch = "refs/heads/1.52"
	}
	var MetaOpenEmbedded = GitVcsRoot {
		id("MetaOpenEmbeddedVCS")
		name = "Meta OpenEmbedded"
		url = "https://github.com/openembedded/meta-openembedded.git"
		branch = "refs/heads/honister"
	}
	var MetaQt5 = GitVcsRoot {
		id("MetaQt5VCS")
		name = "Meta Qt5"
		url = "https://github.com/meta-qt5/meta-qt5"
		branch = "refs/heads/master"
	}
	var MetaSmartphone = GitVcsRoot {
		id("MetaSmartphoneVCS")
		name = "Meta Smartphone"
		url = "https://github.com/shr-distribution/meta-smartphone"
		branch = "refs/heads/honister"
	}
	var MetaAsteroid = GitVcsRoot_fallback {
		id("MetaAsteroidVCS")
		name = "Meta Asteroid"
		gitBase = "https://github.com/"
		url = "${Settings.fork}/meta-asteroid"
		fallback_url = "${Settings.upstream}/meta-asteroid"
		branch = "refs/heads/master"
	}

	// TODO: Move MetaSmartwatch outside CoreVCS
	var MetaSmartwatch = GitVcsRoot_fallback {
		id("MetaSmartwatchVCS")
		name = "Meta Smatwtach"
		gitBase = "https://github.com/"
		url = "${Settings.fork}/meta-smartwatch.git"
		fallback_url = "${Settings.upstream}/meta-smartwatch.git"
		branch = "refs/heads/master"
	}

	// TODO: Switch TempRepository to main asteroid VCS
	var TempRepository = GitVcsRoot {
		id("SettingsVCS")
		name = "Teamcity Asteroid Settings"
		url = "https://github.com/LecrisUT/test-teamcity"
		branch = "refs/heads/lecris.dev"
	}

	fun all(): List<VcsRoot> {
		return listOf(
			Asteroid,
			OpenEmbeddedCore,
			Bitbake,
			MetaOpenEmbedded,
			MetaQt5,
			MetaSmartphone,
			MetaAsteroid,
			MetaSmartwatch,
			TempRepository
		)
	}

	fun attachVCS(init: VcsSettings, forDevice: Boolean = false) {
		init.root(OpenEmbeddedCore, "+:.=>src/oe-core")
		init.root(Bitbake, "+:.=>src/oe-core/bitbake")
		init.root(MetaOpenEmbedded, "+:.=>src/meta-openembedded")
		init.root(MetaQt5, "+:.=>src/meta-qt5")
		init.root(MetaSmartphone, "+:.=>src/meta-smartphone")
		init.root(MetaAsteroid, "+:.=>src/meta-asteroid")
		if (forDevice)
			init.root(MetaSmartwatch, "+:.=>src/meta-smartwatch")

		init.cleanCheckout = true
	}

	open class GitVcsRoot_fallback(init: GitVcsRoot_fallback.() -> Unit) : GitVcsRoot() {
		var gitBase: String? = null
		var fallback_url: String? = null
		init {
			init.invoke(this)
			if (!gitBase.isNullOrEmpty()){
				url = gitBase + url
				if (!fallback_url.isNullOrEmpty())
					fallback_url = gitBase + fallback_url
			}
			if (!fallback_url.isNullOrEmpty()) {
				var testURL = URL(url)
				var con = testURL.openConnection() as HttpURLConnection
				if (con.responseCode == 404) {
					testURL = URL(fallback_url)
					con = testURL.openConnection() as HttpURLConnection
				}
				if (con.responseCode != 200) {

				}
				url = testURL.toString()
			}
		}
	}
}
