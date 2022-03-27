package Devices.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object Devices_MetaSmatwtach : GitVcsRoot({
    name = "Meta Smatwtach"
    url = "git@github.com:AsteroidOS/meta-smartwatch.git"
    branch = "master"
    authMethod = uploadedKey {
        uploadedKey = "Github Deploy Key"
    }
})
