plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.dokka") version "1.9.20"
    `maven-publish`
}

group = rootProject.group
version = rootProject.version

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.slf4j)

    implementation(libs.slf4j.api)

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
    }

    publications {
        create<MavenPublication>("kraftLib") {
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