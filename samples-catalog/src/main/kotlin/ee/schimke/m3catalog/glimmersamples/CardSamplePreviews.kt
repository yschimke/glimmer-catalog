package ee.schimke.m3catalog.glimmersamples

import androidx.compose.runtime.Composable
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.samples.ClickableCardSample
import androidx.xr.glimmer.samples.ClickableCardWithTitleAndSubtitleAndLeadingIconSample
import androidx.xr.glimmer.samples.ClickableCardWithTrailingIconSample
import androidx.xr.glimmer.samples.ClickableImageCardSample
import androidx.xr.glimmer.samples.ClickableImageCardWithTitleAndSubtitleAndLeadingIconSample

// Upstream's `CardSamples.kt` previews only some of its `@Sampled` functions. These render the
// rest, outside the vendored tree, in the same shape as upstream's own previews; see
// `AlertDialogSamplePreviews.kt`.

@GlimmerSamplePreview
@Composable
private fun ClickableCardSamplePreview() {
  GlimmerTheme { ClickableCardSample() }
}

@GlimmerSamplePreview
@Composable
private fun ClickableCardWithTrailingIconSamplePreview() {
  GlimmerTheme { ClickableCardWithTrailingIconSample() }
}

@GlimmerSamplePreview
@Composable
private fun ClickableCardWithTitleAndSubtitleAndLeadingIconSamplePreview() {
  GlimmerTheme { ClickableCardWithTitleAndSubtitleAndLeadingIconSample() }
}

@GlimmerSamplePreview
@Composable
private fun ClickableImageCardSamplePreview() {
  GlimmerTheme { ClickableImageCardSample() }
}

@GlimmerSamplePreview
@Composable
private fun ClickableImageCardWithTitleAndSubtitleAndLeadingIconSamplePreview() {
  GlimmerTheme { ClickableImageCardWithTitleAndSubtitleAndLeadingIconSample() }
}
