import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = rootProject.group
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

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    linuxX64()
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.reflect)
                
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

//val sourcesJar by tasks.registering(Jar::class) {
//    archiveClassifier.set("sources")
//    from(sourceSets.jvmMain.get().allSource)
//}

val dokkaHtmlJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

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
    
    coordinates(group.toString(), "kraft", version.toString())
    
    pom {
        name = "Kraft Core"
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