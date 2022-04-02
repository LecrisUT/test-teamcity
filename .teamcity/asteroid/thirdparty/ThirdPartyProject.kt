package asteroid.thirdparty

import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object ThirdPartyProject : Project({
    id("asteroid/thirdparty")
    name = "ThirdParty Dependencies"
    description = "Third party dependencies"
})
