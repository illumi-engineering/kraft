plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = rootProject.group
version = rootProject.version


kotlin {
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core"))
                implementation(project(":kraftx:logging"))

                implementation(libs.kotlinx.coroutines.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jvmMain {
            dependencies {
            }
        }
    }
}