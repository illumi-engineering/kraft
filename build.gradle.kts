plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

group = "sh.illumi.oss.lib"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}

publishing {
    repositories {
        mavenLocal()
    }

    publications {
        create<MavenPublication>("kraftLib") {
            from(components["java"])
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(19)
}