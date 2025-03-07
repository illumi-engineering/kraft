plugins {
    kotlin("jvm")
    alias(libs.plugins.dokka)
    id("org.jetbrains.kotlinx.atomicfu")
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = rootProject.group
version = rootProject.version


dependencies {
    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.slf4j)

    implementation(libs.slf4j.api)

    implementation(libs.ktor.server.core)

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
            url = uri("https://repo.frotting.services/repository/maven-snapshots/")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
        maven {
            name = "frottingServicesReleases"
            url = uri("https://repo.frotting.services/repository/maven-releases/")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }

    publications {
        create<MavenPublication>("kraftxKtor") {
            artifactId = "kraftx-ktor"

            from(components["java"])

            artifact(sourcesJar.get())
            artifact(dokkaHtmlJar.get())
            artifact(dokkaJavadocJar.get())

            pom {
                name = "KRAFT Extensions for Ktor"
                description = "KRAFT - Kotlin Resource Assembly and Flow Toolkit"
                url = "https://git.lizainslie.dev/illumi/kraft"

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }

                developers {
                    developer {
                        id = "lizainslie"
                        name = "Liz Ainslie"
                        email = "lizzy@lizainslie.dev"
                        url = "https://lizainslie.dev"
                    }
                }

                scm {
                    connection = "scm:git:git://git.lizainslie.dev/illumi/kraft.git"
                    developerConnection = "scm:git:ssh://git.lizainslie.dev/illumi/kraft.git"
                    url = "https://git.lizainslie.dev/illumi/kraft"
                }
            }
        }
    }
}

kotlin {
    jvmToolchain(21)
}