plugins {
    kotlin("jvm") version "2.0.0" apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("org.jetbrains.kotlinx.atomicfu") version "0.26.0" apply false
    `maven-publish`
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
