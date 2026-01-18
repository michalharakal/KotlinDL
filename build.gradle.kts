plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    id("maven-publish")
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.gradle.plugin.publish) apply false
    alias(libs.plugins.dokka)
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

apply(from = rootProject.file("gradle/fatJar.gradle"))
apply(from = rootProject.file("gradle/dokka.gradle"))

val unpublishedSubprojects = listOf("examples", "gradlePlugin")
subprojects {
    if (name in unpublishedSubprojects) return@subprojects
    apply(from = rootProject.file("gradle/publish.gradle"))
}

val sonatypeUser: String? = System.getenv("SONATYPE_USER")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")

nexusPublishing {
    packageGroup.set(project.group.toString())
    repositories {
        sonatype {
            username.set(sonatypeUser)
            password.set(sonatypePassword)
            repositoryDescription.set("kotlinx.kotlindl staging repository, version: $version")
        }
    }
}

tasks.register("setTeamcityVersion") {
    doLast {
        println("##teamcity[buildNumber '$version']")
    }
}
