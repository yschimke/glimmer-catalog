pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
  providers.gradleProperty("composeAiToolsDir").orNull?.let { includeBuild(file(it)) }
}

dependencyResolutionManagement {
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "glimmer-catalog"

include(":catalog")

include(":samples-catalog")

// The pinned AndroidX sources compiled locally for Compose Multiplatform. The two catalogs keep
// rendering the released Android AAR through Robolectric; this port is a separate artifact.
include(":vendor:glimmer")

include(":glimmer-desktop")
