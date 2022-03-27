package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object MetaOpenEmbedded : GitVcsRoot({
    name = "Meta OpenEmbedded"
    url = "https://github.com/openembedded/meta-openembedded.git"
    branch = "refs/heads/honister"
})
