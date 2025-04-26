plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

group = "sh.illumi.kraft"
version = "0.0.2"

allprojects {
    repositories {
        maven("https://repo.frotting.services/repository/maven-releases/") {
            name = "frottingServicesReleases"
        }

        maven("https://mvn.devos.one/releases") {
            name = "devOS"
        }

        mavenCentral()
        google()
    }
}
