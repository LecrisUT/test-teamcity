package ThirdPartyDependencies

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("ThirdPartyDependencies")
    name = "ThirdParty Dependencies"
    description = "Third party dependencies"
})
