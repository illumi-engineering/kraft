plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
    `maven-publish`
}

group = "sh.illumi.kraft"
version = "0.0.1"

allprojects {
    repositories {
        maven {
            name = "frotting-services"
            url = uri("https://maven.frotting.services/releases")
        }

        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
