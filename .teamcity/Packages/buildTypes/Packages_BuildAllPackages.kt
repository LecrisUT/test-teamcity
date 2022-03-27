package Packages.buildTypes

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object Packages_BuildAllPackages : BuildType({
    name = "Build All Packages"

    dependencies {
        snapshot(Packages_AsteroidApps_AsteroidAlarmclock.buildTypes.Packages_AsteroidApps_AsteroidAlarmclock_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidCalculator.buildTypes.Packages_AsteroidApps_AsteroidCalculator_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidCalendar.buildTypes.Packages_AsteroidApps_AsteroidCalendar_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidCamera.buildTypes.Packages_AsteroidApps_AsteroidCamera_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidCompass.buildTypes.Packages_AsteroidApps_AsteroidCompass_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidDiamonds.buildTypes.Packages_AsteroidApps_AsteroidDiamonds_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidFlashlight.buildTypes.Packages_AsteroidApps_AsteroidFlashlight_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidHrm.buildTypes.Packages_AsteroidApps_AsteroidHrm_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidLauncher.buildTypes.Packages_AsteroidApps_AsteroidLauncher_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidMusic.buildTypes.Packages_AsteroidApps_AsteroidMusic_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidSettings.buildTypes.Packages_AsteroidApps_AsteroidSettings_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidStopwatch.buildTypes.Packages_AsteroidApps_AsteroidStopwatch_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidTimer.buildTypes.Packages_AsteroidApps_AsteroidTimer_BuildPackage) {
        }
        snapshot(Packages_AsteroidApps_AsteroidWeather.buildTypes.Packages_AsteroidApps_AsteroidWeather_BuildPackage) {
        }
        snapshot(Packages_AsteroidBtsyncd.buildTypes.Packages_AsteroidBtsyncd_BuildPackage) {
        }
        snapshot(Packages_AsteroidIconsIon.buildTypes.Packages_AsteroidIconsIon_BuildPackage) {
        }
        snapshot(Packages_AsteroidWallpapers.buildTypes.Packages_AsteroidWallpapers_BuildPackage) {
        }
        snapshot(Packages_QmlAsteroid.buildTypes.Packages_QmlAsteroid_BuildPackage) {
        }
        snapshot(Packages_SupportedLanguages.buildTypes.Packages_SupportedLanguages_BuildPackage) {
        }
    }
})
