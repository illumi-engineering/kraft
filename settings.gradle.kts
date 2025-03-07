pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        
        maven("https://repo.frotting.services/repository/maven-releases/") {
            name = "frottingServicesReleases"
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "kraft"

include("core")

include("kraftx:ktor")
include("kraftx:exposed")
include("core-mpp")
