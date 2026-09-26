import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.compose.multiplatform)
  alias(libs.plugins.compose.compiler)
  `maven-publish`
}

kotlin {
  jvm { compilerOptions { jvmTarget.set(JvmTarget.JVM_17) } }
  wasmJs { browser() }

  sourceSets {
    commonMain.dependencies {
      api(libs.compose.runtime)
      api(libs.compose.ui)
      api(libs.compose.foundation)
      api(libs.compose.animation)
      api(libs.androidx.annotation)
    }
    commonTest.dependencies { implementation(libs.kotlin.test) }
  }
}

publishing {
  publications.withType<MavenPublication>().configureEach {
    pom {
      name.set("Compose Glimmer for Compose Multiplatform")
      description.set("AndroidX Glimmer 1.0.0-alpha19 ported to Compose Multiplatform")
      url.set("https://github.com/yschimke/glimmer-catalog/tree/main/vendor")
      licenses {
        license {
          name.set("The Apache License, Version 2.0")
          url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
        }
      }
    }
  }
  repositories {
    maven(rootProject.layout.buildDirectory.dir("glimmer-cmp-maven")) { name = "BuildDir" }
  }
}

tasks.withType<com.ncorti.ktfmt.gradle.tasks.KtfmtBaseTask>().configureEach { enabled = false }
