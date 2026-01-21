description = "A KotlinDL library provides Dataset API for better Kotlin programming for Deep Learning."

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
                api(project(":impl"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmMain by getting {
            dependencies {
                // JVM-specific dataset utilities (file I/O, image loading, etc.)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.bundles.junit)
            }
        }

        val androidMain by getting {
            dependencies {
                // Android-specific dataset utilities
            }
        }

        val nativeMain by getting {
            dependencies {
                // Native-specific dataset utilities
            }
        }

        val jsMain by getting {
            dependencies {
                // JS-specific dataset utilities
            }
        }

        val wasmJsMain by getting {
            dependencies {
                // Wasm-specific dataset utilities
            }
        }
    }
}

android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "org.jetbrains.kotlinx.dl.dataset"
    defaultConfig {
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
