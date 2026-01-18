plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.dokka) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.binary.compatibility.validator) apply false
}

val majorVersion: String by project
val minorVersion: String by project
val kotlinDLVersion = "$majorVersion.$minorVersion"

allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal()  // SKaiNET from local Maven
    }

    group = "org.jetbrains.kotlinx"
    version = kotlinDLVersion
}
