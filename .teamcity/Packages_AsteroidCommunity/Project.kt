package Packages_AsteroidCommunity

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidCommunity")
    name = "Asteroid Community"
    description = "Community managed packages"

    params {
        param("system.recipeLayer", "meta-asteroid-community")
    }
})
