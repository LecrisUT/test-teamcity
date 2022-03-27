package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object Bitbake : GitVcsRoot({
    name = "Bitbake"
    url = "https://github.com/openembedded/bitbake.git"
    branch = "refs/heads/1.52"
})
