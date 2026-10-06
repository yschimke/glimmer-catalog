package ee.schimke.m3catalog.glimmersamples

import androidx.compose.runtime.Composable
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.samples.VoiceInputIndicatorSampleUsage

// Upstream's `VoiceInputIndicatorSamples.kt` previews only some of its `@Sampled` functions. These
// render the
// rest, outside the vendored tree, in the same shape as upstream's own previews; see
// `AlertDialogSamplePreviews.kt`.

@GlimmerSamplePreview
@Composable
private fun VoiceInputIndicatorSampleUsagePreview() {
  GlimmerTheme { VoiceInputIndicatorSampleUsage() }
}
