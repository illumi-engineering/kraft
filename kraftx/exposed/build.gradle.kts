import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = rootProject.group
version = rootProject.version

kotlin {
    jvm()
    
    sourceSets {
        jvmMain {
            dependencies {

                implementation(project(":core"))

                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.slf4j)

                implementation(libs.slf4j.api)

                implementation(libs.exposed.core)
                implementation(libs.exposed.jdbc)

                implementation(libs.hikari)
            }
        }
        
        jvmTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

//val sourcesJar by tasks.registering(Jar::class) {
//    archiveClassifier.set("sources")
//    from(sourceSets.jvmMain.get().allSource)
//}

//val dokkaHtmlJar by tasks.registering(Jar::class) {
//    dependsOn(tasks.dokkaHtml)
//    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
//    archiveClassifier.set("html-docs")
//}

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
//        androidVariantsToPublish = listOf("debug", "release"),
    ))
    coordinates(group.toString(), "kraftx-exposed", version.toString())

    pom {
        name = "KRAFT Extensions for Exposed"
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
