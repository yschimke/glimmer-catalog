import groovy.json.JsonSlurper

plugins {
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.compose.multiplatform) apply false
  alias(libs.plugins.composePreview) apply false
  alias(libs.plugins.ktfmt)
}

allprojects {
  apply(plugin = "com.ncorti.ktfmt.gradle")
  ktfmt { googleStyle() }
}

val upstream = JsonSlurper().parse(file("vendor/glimmer-upstream.json")) as Map<*, *>
val glimmerCmpVersion =
  "%s-cmp%02d".format(upstream["release"], (upstream["portRevision"] as Number).toInt())

project(":vendor:glimmer") {
  group = "ee.schimke.glimmercmp"
  version = glimmerCmpVersion
}

tasks.register("publishGlimmerCmpToBuildDir") {
  group = "publishing"
  description = "Publish the vendored Glimmer CMP port into build/glimmer-cmp-maven."
  dependsOn(":vendor:glimmer:publishAllPublicationsToBuildDirRepository")
}

tasks.register("printGlimmerCmpPortVersion") {
  inputs.property("glimmerCmpVersion", glimmerCmpVersion)
  doLast { println(inputs.properties.getValue("glimmerCmpVersion")) }
}

// composePreviewDaemon (the compose-preview-daemon-bom version every ee.schimke.composeai runtime
// module resolves through) must equal the daemon release the compose-preview plugin bakes into its
// jar (`previewDaemon` in plugin-version.properties): the catalogs compile against the same runtime
// the plugin's renderer hosts. Renovate cannot read that value, so it never moves the BOM on its
// own
// (.github/renovate.json); this check is what keeps the two equal when the plugin moves.
val bakedPreviewDaemonVersion: String =
  javaClass.classLoader
    .getResource("ee/schimke/composeai/plugin/plugin-version.properties")
    ?.openStream()
    ?.use { java.util.Properties().apply { load(it) } }
    ?.getProperty("previewDaemon") ?: "<missing from the plugin jar>"

val verifyComposePreviewDaemonAlignment by tasks.registering {
  group = "verification"
  description = "Fail when composePreviewDaemon differs from the compose-preview plugin's daemon."
  val plugin = libs.versions.composePreviewPlugin.get()
  val catalog = libs.versions.composePreviewDaemon.get()
  val bakedPreviewDaemon = bakedPreviewDaemonVersion
  inputs.property("baked", bakedPreviewDaemon)
  inputs.property("catalog", catalog)
  doLast {
    check(bakedPreviewDaemon == catalog) {
      "gradle/libs.versions.toml has composePreviewDaemon = \"$catalog\", but compose-preview " +
        "plugin $plugin bakes daemon \"$bakedPreviewDaemon\" (plugin-version.properties). " +
        "Set composePreviewDaemon = \"$bakedPreviewDaemon\"."
    }
  }
}

subprojects {
  if (path == ":catalog" || path == ":samples-catalog") {
    tasks
      .matching { it.name == "preBuild" }
      .configureEach { dependsOn(verifyComposePreviewDaemonAlignment) }
  }
}
