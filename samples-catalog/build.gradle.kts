// `:samples-catalog` — the AndroidX Glimmer samples, rendered.
//
// Everything under `src/main/kotlin/upstream/` is VENDORED, in upstream's own package
// (`androidx.xr.glimmer.samples`), fetched by `scripts/import-samples.mjs` from the commit pinned
// in `samples-catalog/import.json`. It is never edited in place and never formatted — a fix is a
// patch in `samples-catalog/patches/` with a stated reason.
//
// ── Why these render where m3's samples could not ─────────────────────────────────────────────
//
// `docs/design/ANDROIDX_SAMPLES.md` records that the material3 samples lose 8 files to quarantine
// because the desktop renderer has no `android.os`, no resource table and no `LocalContext`. None
// of that applies here: this module IS Android, and the Glimmer samples import
// `androidx.xr.glimmer`, Compose and nothing else. The quarantine list is empty, and that is the
// expected steady state rather than a lucky first run.
//
// ── Annotation density ────────────────────────────────────────────────────────────────────────
//
// Glimmer's samples carry MORE `@Preview`s than `@Sampled` functions (54 to 47 at the pinned ref),
// the best ratio in AndroidX — so discovery finds them directly and this module, like
// `:samples-catalog`, generates no wrappers.
plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.compose.compiler)
  id("ee.schimke.composeai.preview")
}

composePreview {
  // The pin `:catalog` carries, for the same reason: Robolectric ships shadows to API 36
  // and 36 needs JDK 21+, while 35 runs on the JDK 17 toolchain these modules compile with.
  sdkVersion.set(35)
}

android {
  namespace = "ee.schimke.m3catalog.glimmersamples"
  // glimmer's AAR metadata says `minCompileSdk=37`.
  compileSdk = 37
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
  // `createGoogleSansFlexTypography()`, for the preview wrapper that puts the kit's typeface around
  // the vendored samples. `:catalog` carries the same pair for the same reason.
  implementation(libs.glimmer.google.fonts)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.ui.tooling)
  // The ee.schimke.composeai runtime modules are versionless; the daemon BOM picks each one's
  // latest published version for the daemon release in gradle/libs.versions.toml.
  implementation(platform(libs.composeai.daemon.bom))
  implementation(libs.composeai.preview.annotations)
}

// The vendored tree is upstream's bytes. ktfmt would rewrite it into a permanent diff against every
// future import, so it is excluded — the formatting counterpart of "a fix is a patch, never an
// edit". Same narrowing `:samples-catalog` applies.
tasks.withType<com.ncorti.ktfmt.gradle.tasks.KtfmtBaseTask>().configureEach {
  exclude { it.file.absolutePath.replace('\\', '/').contains("/src/main/kotlin/upstream/") }
}
