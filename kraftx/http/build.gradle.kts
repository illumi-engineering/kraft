plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    `maven-publish`
}

group = rootProject.group
version = rootProject.version


dependencies {
    implementation(project(":core"))

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.slf4j)

    implementation(libs.slf4j.api)

    implementation("io.netty:netty-all:4.1.114.Final") // todo: add to version catalog
    implementation("com.oxyggen.net:urilib:1.0.11") // todo: add to version catalog

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
        create<MavenPublication>("kraftxHttp") {
            artifactId = "kraftx-http"

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