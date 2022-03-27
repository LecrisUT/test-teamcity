package _Self.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object MetaQt5 : GitVcsRoot({
    name = "Meta Qt5"
    url = "https://github.com/meta-qt5/meta-qt5"
    branch = "refs/heads/master"
})
