package Packages

import Packages.buildTypes.*
import Packages.vcsRoots.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages")
    name = "Packages"
    description = "Global packages"

    vcsRoot(Packages_PackageSource)

    buildType(Packages_BuildAllPackages)

    template(Packages_BuildPackage)

    params {
        text("PackageVCS_DefaultBranch", "refs/heads/master", label = "Default branch", description = "Default branch of the git repository", allowEmpty = false)
        text("system.recipeLayer", "meta-asteroid", label = "Recipe layer", description = "The bitbake layer location", allowEmpty = false)
        text("PackageVCS_URL", "git@github.com:AsteroidOS/%system.recipeName%.git", label = "Repository URL", description = "Git URL managing the package", allowEmpty = false)
        text("system.recipeName", "%system.teamcity.projectName%", label = "Package name", description = "The name of the recipe in bitbake", allowEmpty = false)
    }

    subProject(Packages_AsteroidApps.Project)
    subProject(Packages_AsteroidBtsyncd.Project)
    subProject(Packages_AsteroidWallpapers.Project)
    subProject(Packages_AsteroidCommunity.Project)
    subProject(Packages_SupportedLanguages.Project)
    subProject(Packages_AsteroidIconsIon.Project)
    subProject(Packages_QmlAsteroid.Project)
})
