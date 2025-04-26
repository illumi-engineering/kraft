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
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "kraft"

include(":core")

// KRAFT Extensions
include(":kraftx:ktor")
include(":kraftx:exposed")

// KRAFT Extensions for Logging
include(":kraftx:logging:core")

include(":kraftx:logging:prettylog")
// todo: slf4j

// Examples
include(":examples:playground-mpp")
include(":examples:kraftx:logging")