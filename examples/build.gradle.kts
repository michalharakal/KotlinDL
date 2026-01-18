plugins {
    kotlin("jvm")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    api(project(":api"))
    api(project(":impl"))
    api(project(":dataset"))

    implementation("org.apache.logging.log4j:log4j-api:2.17.2")
    implementation("org.apache.logging.log4j:log4j-core:2.17.2")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
}

val publishedArtifactsVersion: String? = System.getenv("KOTLIN_DL_RELEASE_VERSION")
if (!publishedArtifactsVersion.isNullOrBlank()) {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(project(":api")).using(module("org.jetbrains.kotlinx:kotlin-deeplearning-api:$publishedArtifactsVersion"))
            substitute(project(":impl")).using(module("org.jetbrains.kotlinx:kotlin-deeplearning-impl:$publishedArtifactsVersion"))
            substitute(project(":dataset")).using(module("org.jetbrains.kotlinx:kotlin-deeplearning-dataset:$publishedArtifactsVersion"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
    minHeapSize = "1024m"
    maxHeapSize = "8g"
}
