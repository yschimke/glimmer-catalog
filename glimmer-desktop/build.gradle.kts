plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
}

dependencies {
  implementation(project(":vendor:glimmer"))
  implementation(compose.desktop.currentOs)
  testImplementation(libs.kotlin.test.junit)
  testImplementation(libs.compose.ui.test)
}

tasks.test {
  systemProperty(
    "glimmer.desktop.out",
    layout.buildDirectory.dir("glimmer-desktop").get().asFile.path,
  )
}
