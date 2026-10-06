package ee.schimke.m3catalog.glimmersamples

import androidx.compose.runtime.Composable
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.samples.GlimmerPagerStateAnimateScrollToPageSample
import androidx.xr.glimmer.samples.GlimmerPagerStateCustomAnimateScrollToPageSample
import androidx.xr.glimmer.samples.GlimmerPagerStateScrollToPageSample

// Upstream's `GlimmerPagerSamples.kt` previews only some of its `@Sampled` functions. These render
// the
// rest, outside the vendored tree, in the same shape as upstream's own previews; see
// `AlertDialogSamplePreviews.kt`.

@GlimmerSamplePreview
@Composable
private fun GlimmerPagerStateAnimateScrollToPageSamplePreview() {
  GlimmerTheme { GlimmerPagerStateAnimateScrollToPageSample() }
}

@GlimmerSamplePreview
@Composable
private fun GlimmerPagerStateScrollToPageSamplePreview() {
  GlimmerTheme { GlimmerPagerStateScrollToPageSample() }
}

@GlimmerSamplePreview
@Composable
private fun GlimmerPagerStateCustomAnimateScrollToPageSamplePreview() {
  GlimmerTheme { GlimmerPagerStateCustomAnimateScrollToPageSample() }
}
