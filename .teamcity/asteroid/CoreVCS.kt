package asteroid

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.json.responseJson
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsRoot
import jetbrains.buildServer.configs.kotlin.v2019_2.VcsSettings
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot


object CoreVCS {
	var Asteroid: GitVcsRoot = GitVcsRoot_fallback {
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
	var MetaAsteroid: GitVcsRoot = GitVcsRoot_fallback {
		id("MetaAsteroidVCS")
		name = "Meta Asteroid"
		gitBase = "https://github.com/"
		url = "${Settings.fork}/meta-asteroid"
		fallback_url = "${Settings.upstream}/meta-asteroid"
		branch = "refs/heads/master"
	}

	// TODO: Move MetaSmartwatch outside CoreVCS
	var MetaSmartwatch: GitVcsRoot = GitVcsRoot_fallback {
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
			if (!gitBase.isNullOrEmpty()) {
				url = gitBase + url
				if (!fallback_url.isNullOrEmpty())
					fallback_url = gitBase + fallback_url
			}
			if (!fallback_url.isNullOrEmpty()) {
				if (Settings.canHttp) {
					var testURL: String = url ?: ""
					var code = Fuel.get(testURL).response().second.statusCode
					if (code == 404) {
						testURL = fallback_url ?: ""
						code = Fuel.get(testURL).response().second.statusCode
					}
					if (code != 200) {
						// TODO: Resolve other excetions
					}
					url = testURL
				} else
					url = fallback_url
			}
		}
	}

	enum class GitRepoHubType {
		Github
	}

	interface GitAPIChecker {
		val repo: String
		val token: String
		val hubType: GitRepoHubType
		val commitUser: String
		fun checkCommitStatus(): Boolean
		fun checkPR(): Boolean
		fun checkToken(): Boolean

		companion object {
			fun Create(repo: String, token: String): GitAPIChecker? {
				with(repo) {
					when {
						contains("https://github.com") -> return GithubAPIChecker(repo, token)
						else -> return null
					}
				}
			}
		}

		open class GithubAPIChecker(override val repo: String, override val token: String) : GitAPIChecker {
			override val hubType = GitRepoHubType.Github
			override val commitUser = Settings.commitUser
			val repoAPIBase = repo.removeSuffix(".git")
				.replace("https://github.com", "https://api.github.com/repos")

			override fun checkToken(): Boolean {
				// Cannot check if sandboxing is enabled or the token is not plain-text
				if (!Settings.canHttp || token.startsWith("credentialsJSON:"))
					return false
				val request = Fuel.get("https://api.github.com")
					.appendHeader(Headers.AUTHORIZATION, "token $token")
				return when (request.response().second.statusCode) {
					401 -> false
					200 -> true
					else -> {
						// TODO: Add warning
						false
					}
				}
			}

			override fun checkCommitStatus(): Boolean {
				// Cannot check if sandboxing is enabled or the token is not plain-text
				if (!Settings.canHttp || token.startsWith("credentialsJSON:"))
					return true
				// If token is invalid throw warning
				if (!checkToken()) {
					// TODO: add warning
					return false
				}

				var request = Fuel.get("$repoAPIBase/commits/HEAD/status")
					.appendHeader(Headers.AUTHORIZATION, "token $token")
				var response = request.responseJson()
				when (response.second.statusCode) {
					// Token cannot access private repo
					404 -> return false
					// Token does not have repo:status:read access to the repo
					403 -> return false
					// Token has at least repo:status:read access to the repo
					200 -> {}
					else -> {
						// Unknown states
						// TODO: Add warning
						return false
					}
				}
				val sha = response.third.component1()!!.obj()["sha"].toString()
				request = Fuel.post("$repoAPIBase/commits/$sha/statuses")
					.appendHeader(Headers.AUTHORIZATION, "token $token")
					.jsonBody(
						"""
						{
							"context": "test-connection",
							"state": "dummy"
						}
					""".trimIndent()
					)
				response = request.responseJson()
				return when (response.second.statusCode) {
					// Token does not have repo:status:write access to the repo
					403 -> false
					// Token has repo:status:write access but we made ill-formed content
					422 -> true
					// Created status. This should not have occured
					201 -> {
						// TODO: Add warning
						true
					}
					// Unknown
					else -> false
				}
			}

			override fun checkPR(): Boolean {
				// Cannot check if sandboxing is enabled or the token is not plain-text
				if (!Settings.canHttp || token.startsWith("credentialsJSON:"))
					return true
				// If token is invalid throw warning
				if (!checkToken()) {
					// TODO: add warning
					return false
				}

				val request = Fuel.get("$repoAPIBase/pulls")
					.appendHeader(Headers.AUTHORIZATION, "token $token")
				return when (request.response().second.statusCode) {
					// Token cannot access private repo
					404 -> false
					// Token does not have repo:status:read access to the repo
					403 -> false
					// Token has at least repo:status:read access to the repo
					200 -> true
					else -> {
						// Unknown states
						// TODO: Add warning
						false
					}
				}
			}
		}
	}
}
