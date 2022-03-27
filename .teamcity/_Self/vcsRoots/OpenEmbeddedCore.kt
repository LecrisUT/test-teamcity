package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object OpenEmbeddedCore : GitVcsRoot({
    name = "OpenEmbedded Core"
    url = "https://github.com/openembedded/openembedded-core.git"
    branch = "refs/heads/honister"
})
