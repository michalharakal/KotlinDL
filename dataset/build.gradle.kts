description = "A KotlinDL library provides Dataset API for better Kotlin programming for Deep Learning."

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())

    jvm()

    sourceSets {
        val jvmMain by getting {
            dependencies {
                api(project(":api"))
                api(project(":impl"))
            }
        }
    }
    explicitApiWarning()
}
