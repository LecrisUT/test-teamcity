package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object MetaSmartphone : GitVcsRoot({
    name = "Meta Smartphone"
    url = "https://github.com/shr-distribution/meta-smartphone"
    branch = "refs/heads/honister"
})
