pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        mavenLocal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android" || requested.id.name == "kotlin-android-extensions") {
                useModule("com.android.tools.build:gradle:8.2.0")
            }
        }
    }
}

rootProject.name = "KotlinDL"
include("api")
include("impl")
include("dataset")
// Temporarily excluded - examples depend on removed tensorflow module
// include("examples")
// Removed: tensorflow, onnx, visualization (depend on tf)
