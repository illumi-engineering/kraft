import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.dokka)
}

group = "sh.illumi.kraftx"
version = rootProject.version

kotlin {
    jvm()

    androidTarget("android") {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    mingwX64()

    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":kraftx:logging:core"))

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.datetime)
                implementation(libs.okio)
                implementation(libs.prettylog)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

//val dokkaHtmlJar by tasks.registering(Jar::class) {
//    dependsOn(tasks.dokkaHtml)
//    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
//    archiveClassifier.set("html-docs")
//}

android {
    namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

publishing {
    repositories {
        mavenLocal()
        maven {
            name = "frottingServicesSnapshots"
            url = uri("https://repo.frotting.services/repository/maven-snapshots/")
            credentials(PasswordCredentials::class)
        }
        maven {
            name = "frottingServicesReleases"
            url = uri("https://repo.frotting.services/repository/maven-releases/")
            credentials(PasswordCredentials::class)
        }
    }
}

mavenPublishing {
    configure(KotlinMultiplatform(
        javadocJar = JavadocJar.Dokka("dokkaHtml"),
        sourcesJar = true,
        androidVariantsToPublish = listOf("debug", "release"),
    ))

    coordinates(group.toString(), "kraftx-logging-prettylog", version.toString())

    pom {
        name = "kraftx-logging prettylog integration"
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