description = "This module contains the Kotlin API for building, training, and evaluating the Deep Learning models."

plugins {
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(libs.versions.jvmTarget.get().toInt())
    explicitApiWarning()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    archives(sourcesJar)
}
