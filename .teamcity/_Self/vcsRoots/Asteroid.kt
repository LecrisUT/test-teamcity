package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object Asteroid : GitVcsRoot({
    name = "Asteroid"
    url = "git@github.com:LecrisUT/asteroid.git"
    branch = "refs/heads/add-CI"
    authMethod = uploadedKey {
        uploadedKey = "Github Deploy Key"
    }
})
