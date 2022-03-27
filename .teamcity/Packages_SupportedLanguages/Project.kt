package Packages_SupportedLanguages

import Packages_SupportedLanguages.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_SupportedLanguages")
    name = "Supported Languages"
    defaultTemplate = RelativeId("Packages_BuildPackage")

    buildType(Packages_SupportedLanguages_BuildPackage)

    params {
        param("system.recipeName", "supported-languages")
    }
})
