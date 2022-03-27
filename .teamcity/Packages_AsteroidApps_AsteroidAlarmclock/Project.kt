package Packages_AsteroidApps_AsteroidAlarmclock

import Packages_AsteroidApps_AsteroidAlarmclock.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps_AsteroidAlarmclock")
    name = "Asteroid Alarmclock"

    buildType(Packages_AsteroidApps_AsteroidAlarmclock_BuildPackage)

    params {
        param("system.recipeName", "asteroid-alarmclock")
    }
})
