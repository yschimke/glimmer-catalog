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
