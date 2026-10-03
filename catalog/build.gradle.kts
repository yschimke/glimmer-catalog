// `:catalog` — `androidx.xr.glimmer` rebuilt as `@Preview` stickers.
//
// ── Why this one IS an Android module ─────────────────────────────────────────────────────────
//
// The released `androidx.xr.glimmer:glimmer` is an Android AAR (`minCompileSdk=37`), so the
// authoritative catalog uses AGP and the Robolectric renderer. The local CMP port is deliberately
// separate: it proves portability and backs future browser rendering, but it is not substituted
// for the library this catalog documents.
//
// ── A library module ──────────────────────────────────────────────────────────────────────────
//
// Nothing here is installed, so there is no application to declare: the Robolectric lane renders a
// library module directly, which is the shape compose-ai-tools' own `:samples:xr-glimmer` uses.
plugins {
  // No Kotlin plugin of its own: AGP 9 brings Kotlin support with it, and applying
  // `org.jetbrains.kotlin.android` on top fails outright — KGP's Android target reaches for
  // `com.android.build.gradle.api.BaseVariant`, the old variant API AGP 9 removed.
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose.compiler)
  id("ee.schimke.composeai.preview")
}

composePreview {
  // Robolectric ships up to API 36, and 36 needs JDK 21+; 35 runs on the JDK 17 toolchain this
  // module compiles with. Both Android catalog modules use the same pin.
  sdkVersion.set(35)
}

android {
  namespace = "ee.schimke.m3catalog.glimmer"
  // glimmer 1.0.0-alpha19's AAR metadata says `minCompileSdk=37`; anything lower fails to resolve.
  compileSdk = 37

  // The AAR's own `uses-sdk` floor.
  defaultConfig { minSdk = 24 }

  buildFeatures { compose = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  testOptions { unitTests { isIncludeAndroidResources = true } }
}

dependencies {
  implementation(libs.glimmer)
  // `createGoogleSansFlexTypography()` — the kit's own typeface, resolved through Android's
  // downloadable-font provider and cached in `~/.cache/composeai/fonts`. See the note beside this
  // artifact in `gradle/libs.versions.toml` for why the render can depend on a downloaded face.
  implementation(libs.glimmer.google.fonts)
  // `androidx.compose.ui.tooling.preview.Preview` — the FQN discovery scans for. The ANDROIDX
  // artifact, not the Compose Multiplatform republication the desktop modules use.
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.ui.tooling)
  // The ee.schimke.composeai runtime modules are versionless; the daemon BOM picks each one's
  // latest published version for the daemon release in gradle/libs.versions.toml.
  implementation(platform(libs.composeai.daemon.bom))
  implementation(libs.composeai.preview.annotations)
  // `previewOverrideBoolean`, which backs the `clickCount` knob `counted` exposes — the same
  // live-lane contract `:catalog` carries. See GlimmerInteractive.kt.
  implementation(libs.composeai.preview.overrides)
  // The passthrough backdrops behind @GlimmerEnvironmentPreview.
  implementation(libs.composeai.glimmer.environment)
  // The glyphs the kit draws. Every icon slot in the Glimmer kit is a Material Symbol named by
  // ligature in the node itself — `send` on the buttons and the list item, `mic_off` on the icon
  // buttons — so the icons are the kit's choice rather than this catalog's, and they come from the
  // published set rather than being redrawn here.
  implementation(libs.androidx.compose.material.icons.extended)
  testImplementation(libs.robolectric)
  // `GlimmerTranslationsTest` reads `src/main/res` off disk, so it needs no Android runtime.
  testImplementation(libs.kotlin.test.junit)
}
