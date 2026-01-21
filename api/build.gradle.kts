description = "This module contains the Kotlin API for building, training, and evaluating the Deep Learning models."

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
    explicitApiWarning()

    // JVM target
    jvm()

    // Android target
    androidTarget {
        publishLibraryVariants("release")
    }

    // Native targets - Desktop (SKaiNET supported)
    linuxX64()
    linuxArm64()
    macosArm64()

    // Native targets - iOS (SKaiNET supported)
    iosArm64()
    iosSimulatorArm64()

    // JavaScript target
    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        nodejs()
    }

    // WebAssembly target
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    // Hierarchical project structure
    applyDefaultHierarchyTemplate()

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Pure Kotlin API - no platform-specific dependencies
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "org.jetbrains.kotlinx.dl.api"
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
