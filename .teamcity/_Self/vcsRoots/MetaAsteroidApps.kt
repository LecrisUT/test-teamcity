package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object MetaAsteroidApps : GitVcsRoot({
    name = "Meta Asteroid Apps"
    url = "git@github.com:LecrisUT/meta-asteroid-apps"
    branch = "refs/heads/master"
    authMethod = uploadedKey {
        uploadedKey = "Github Deploy Key"
    }
})
