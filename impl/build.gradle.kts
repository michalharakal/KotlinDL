description = "This module contains the Kotlin API implementation code for building, training, and evaluating the Deep Learning models."

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
    explicitApiWarning()

    // JVM target
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

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
                api(project(":api"))
                // SKaiNET core dependencies (multiplatform)
                api(libs.bundles.skainet.common)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
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

        // Native source sets are created automatically by applyDefaultHierarchyTemplate()
        val nativeMain by getting {
            dependencies {
                // Native-specific dependencies if needed
            }
        }

        val jsMain by getting {
            dependencies {
                // JS-specific dependencies if needed
            }
        }

        val wasmJsMain by getting {
            dependencies {
                // Wasm-specific dependencies if needed
            }
        }
    }
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "org.jetbrains.kotlinx.dl.impl"
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
