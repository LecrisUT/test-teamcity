package Packages.vcsRoots

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object Packages_PackageSource : GitVcsRoot({
    name = "Package Source"
    url = "%PackageVCS_URL%"
    branch = "%PackageVCS_DefaultBranch%"
    authMethod = uploadedKey {
        uploadedKey = "Github Deploy Key"
    }
})
