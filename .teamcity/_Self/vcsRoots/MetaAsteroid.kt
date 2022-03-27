package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object MetaAsteroid : GitVcsRoot({
    name = "Meta Asteroid"
    url = "git@github.com:LecrisUT/meta-asteroid"
    branch = "refs/heads/reorganize"
    authMethod = uploadedKey {
        uploadedKey = "Github Deploy Key"
    }
})
