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

// The catalog-owned UI Builder renderer links the source-only renderer SDK from an immutable
// compose-ui-builder checkout. Ordinary catalog builds do not see the module or the synthetic
// coordinate; the Design Artifacts workflow opts in with -PcomposeUiBuilderDir.
providers.gradleProperty("composeUiBuilderDir").orNull?.let { path ->
  val directory = file(path).canonicalFile
  require(directory.resolve("settings.gradle.kts").isFile) {
    "-PcomposeUiBuilderDir names $directory, which is not a compose-ui-builder Gradle checkout."
  }
  includeBuild(directory) {
    dependencySubstitution {
      substitute(module("ee.schimke.composeai:ui-builder-renderer-sdk-source"))
        .using(project(":ui-builder-renderer-sdk"))
    }
  }
  include(":glimmer-ui-builder-renderer")
}
