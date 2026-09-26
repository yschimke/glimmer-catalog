# Compose Glimmer catalog — as code

The public [Jetpack Compose Glimmer UI kit](https://www.figma.com/design/HKfLClZDLRyMhf4IQQLna8/Jetpack-Compose-Glimmer-UI--Community-)
rebuilt as real `androidx.xr.glimmer` `@Preview`s and published as an importable catalog.

## What is published

| Module | System | Output branch (`yschimke/glimmer-catalog-out`) |
| --- | --- | --- |
| `:catalog` | `glimmer-catalog` | `design-artifacts/glimmer-catalog` |
| `:samples-catalog` | `glimmer-samples` | `design-artifacts/glimmer-samples` |

The catalog is design-led: every published component names an exact Glimmer kit node, the kit is
authoritative, and every Figma interaction is read-only. Both sheets render the released Android
AAR through Robolectric, so their paired images use the same rasterizer.

[`ui-builder.policy.json`](ui-builder.policy.json) publishes the catalog as a glasses UI Builder
palette. Component records come from discovery of the real Glimmer call sites; the policy owns the
platform, frame, shelves, theme roles, and native/wasm fidelity declaration.

## Compose Multiplatform port

[`vendor/glimmer`](vendor/README.md) carries the exact AndroidX Glimmer alpha20 sources jar and
compiles it for Compose Multiplatform (`jvm()` and `wasmJs`). The only visual change is the Android
AGSL surface: common targets use Glimmer's own pre-API-33 solid-border fallback. The remaining edits
remove Android/JVM-only types and expressions without changing the component API or layout.

The port publishes as `ee.schimke.glimmercmp:glimmer:1.0.0-alpha20-cmp01` to the
`glimmer-cmp-maven` branch of `yschimke/glimmer-catalog-out`. The catalogs deliberately keep using
the released AAR; `:glimmer-desktop` proves the fork renders through Skiko without Android or
Robolectric.

## Building

```sh
scripts/agent-gradle.sh :catalog:assemble :catalog:composePreviewDiscover
scripts/agent-gradle.sh :samples-catalog:assemble :samples-catalog:composePreviewDiscover
scripts/agent-gradle.sh :vendor:glimmer:compileKotlinJvm :vendor:glimmer:compileKotlinWasmJs
scripts/agent-gradle.sh :glimmer-desktop:test publishGlimmerCmpToBuildDir
node scripts/samples-spec.mjs --check
scripts/design-map.sh --check
```

The Android catalogs need SDK platform 37.0. See [docs/design/GLIMMER.md](docs/design/GLIMMER.md)
for the renderer, additive-display, kit-mapping, typography, sample, and parity decisions.

## Repository setup

Generated branches live in the separate public `yschimke/glimmer-catalog-out` repository. GitHub
Actions needs an `ARTIFACTS_TOKEN` secret with write access to that repository. `FIGMA_TOKEN` is
optional for ordinary cache-backed publishing, but required to refresh the read-only design-parity
reference cache. No workflow writes to Figma.
