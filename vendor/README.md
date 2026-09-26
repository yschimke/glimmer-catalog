# Vendored Glimmer, ported to Compose Multiplatform

AndroidX publishes `androidx.xr.glimmer:glimmer` as an Android AAR only. This directory carries the
same alpha20 sources compiled for Compose Multiplatform JVM and Wasm.

## Pin

[`glimmer-upstream.json`](glimmer-upstream.json) pins the release's published `-sources.jar` by URL
and SHA-256. The Kotlin under `src/commonMain` is imported from that archive before the portability
edits below, so this is the released alpha20 source rather than a nearby `androidx-main` snapshot.

Published versions are `<release>-cmp<portRevision>` and immutable. Increase `portRevision` for
changed published bytes without a new upstream release.

## Port edits

The source remains in upstream's `androidx.xr.glimmer` package and retains its AOSP headers. Search
for `CMP-PORT` to find the behavioural and expression changes:

1. `Surface.kt` cannot use Android's `RuntimeShader` / `RenderEffect` on common targets. It always
   selects Glimmer's own API-below-33 solid background and border path. Focus, press, depth,
   clipping, content colour, and component layout remain upstream code; only the progressive AGSL
   blur/gradient is absent.
2. `DefaultStackItemKey.kt` drops Android `Parcelable`. It remains the same private index data class
   used for equality and stable identity.
3. `IndirectPointerGesture.kt` reads the current common `IndirectPointerEvent` only; Android
   `MotionEvent` history is unavailable on common targets.
4. `HctUtils.kt` spells radians-to-degrees as common arithmetic instead of `java.lang.Math`.
5. `StackScrimModifier.kt` uses the common class reference rather than JVM `javaClass`.
6. Alpha20's local-bounds focus helper has not reached Compose Multiplatform yet. `ButtonGroup`,
   lazy lists, pagers, and stacks convert the same rectangles to the public root-bounds API, as
   alpha19 did upstream.
7. `AlertDialog.kt` omits Android-only window-fit and scrim properties while retaining the common
   dialog width behavior.

`Button.kt`, `ButtonGroup.kt`, `IconMarker.kt`, and `HctUtils.kt` also import Kotlin's `JvmInline`
explicitly; Android source sets receive that import implicitly, while common source sets do not.

No Android target is published: Android applications should use the real AAR. `:glimmer-desktop`
renders a real Glimmer button and asserts on semantics and pixels. CI compiles both JVM and Wasm and
publishes the Maven tree to `yschimke/glimmer-catalog-out`.

```kotlin
repositories {
  maven("https://raw.githubusercontent.com/yschimke/glimmer-catalog-out/glimmer-cmp-maven/") {
    content { includeGroup("ee.schimke.glimmercmp") }
  }
}

dependencies {
  implementation("ee.schimke.glimmercmp:glimmer:1.0.0-alpha20-cmp01")
}
```
