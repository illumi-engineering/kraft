plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.slf4j)

    implementation(libs.slf4j.api)

    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)

    implementation(libs.hikari)

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlinx.coroutines.test)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

val dokkaJavadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        mavenLocal()
        maven {
            name = "frottingServicesSnapshots"
            url = uri("https://maven.frotting.services/snapshots")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "frottingServicesReleases"
            url = uri("https://maven.frotting.services/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("kraftxExposed") {
            artifactId = "kraftx-exposed"

            from(components["java"])

            artifact(sourcesJar.get())
            artifact(dokkaHtmlJar.get())
            artifact(dokkaJavadocJar.get())

            pom {
                // todo
            }
        }
    }
}

kotlin {
    jvmToolchain(19)
}