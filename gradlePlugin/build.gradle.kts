repositories {
    gradlePluginPortal()
}

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.gradle.plugin.publish)
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
}

dependencies {
    implementation(gradleApi())
    implementation(libs.download.gradle.plugin)
    implementation(libs.android.gradle.plugin)
}

gradlePlugin {
    website.set("https://github.com/Kotlin/kotlindl")
    vcsUrl.set("https://github.com/Kotlin/kotlindl")
    plugins {
        create("kotlinDlGradlePlugin") {
            id = "org.jetbrains.kotlinx.kotlin-deeplearning-gradle-plugin"
            implementationClass = "org.jetbrains.kotlinx.dl.gradle.DownloadModelsPlugin"
            displayName = "KotlinDL Gradle Plugin"
            description = "Adds a task for downloading Kotlin DL pretrained models from the model hub"
            tags.set(listOf("kotlin", "deep-learning"))
        }
    }
}
