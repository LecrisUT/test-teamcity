package Packages_AsteroidApps

import Packages_AsteroidApps.buildTypes.*
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

object Project : Project({
    id("Packages_AsteroidApps")
    name = "Asteroid Apps"
    description = "Core AsteroidOS packages"
    defaultTemplate = RelativeId("Packages_AsteroidApps_BuildPackage")

    template(Packages_AsteroidApps_BuildPackage)

    params {
        param("system.recipeLayer", "meta-asteroid-apps")
    }

    subProject(Packages_AsteroidApps_AsteroidSettings.Project)
    subProject(Packages_AsteroidApps_AsteroidAlarmclock.Project)
    subProject(Packages_AsteroidApps_AsteroidStopwatch.Project)
    subProject(Packages_AsteroidApps_AsteroidFlashlight.Project)
    subProject(Packages_AsteroidApps_AsteroidCompass.Project)
    subProject(Packages_AsteroidApps_AsteroidCalendar.Project)
    subProject(Packages_AsteroidApps_AsteroidHrm.Project)
    subProject(Packages_AsteroidApps_AsteroidWeather.Project)
    subProject(Packages_AsteroidApps_AsteroidLauncher.Project)
    subProject(Packages_AsteroidApps_AsteroidDiamonds.Project)
    subProject(Packages_AsteroidApps_AsteroidMusic.Project)
    subProject(Packages_AsteroidApps_AsteroidTimer.Project)
    subProject(Packages_AsteroidApps_AsteroidCalculator.Project)
    subProject(Packages_AsteroidApps_AsteroidCamera.Project)
})
