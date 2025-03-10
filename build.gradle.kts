plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

group = "sh.illumi.kraft"
version = "0.0.1"

allprojects {
    repositories {
        maven("https://repo.frotting.services/repository/maven-releases/") {
            name = "frottingServicesReleases"
        }

        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
