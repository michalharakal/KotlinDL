description = "This module contains the Kotlin API implementation code for building, training, and evaluating the Deep Learning models."

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())

    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    androidTarget {
        publishLibraryVariants("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":api"))
                // SKaiNET core dependencies
                api(libs.bundles.skainet.common)
            }
        }
        val jvmMain by getting {
            dependencies {
                api(libs.kotlin.csv)
                api(libs.kotlin.logging)
                api(libs.jhdf)
                api(libs.klaxon)
                implementation(libs.imageio.jpeg)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.bundles.junit)
                implementation(libs.kotlin.test)
                // SKaiNET JVM-specific for tests
                implementation(libs.bundles.skainet.jvm)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.camera.core)
            }
        }
    }
    explicitApiWarning()
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "org.jetbrains.kotlinx.dl.impl"
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
