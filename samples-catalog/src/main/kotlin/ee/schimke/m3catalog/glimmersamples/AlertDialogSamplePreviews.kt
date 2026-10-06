package ee.schimke.m3catalog.glimmersamples

import androidx.compose.runtime.Composable
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.samples.AlertDialogConfirmOnlySample
import androidx.xr.glimmer.samples.AlertDialogSample

// Upstream's `AlertDialogSamples.kt` is the one vendored sample file with no `@Preview` at all, so
// without these the samples sheet carried no render of `AlertDialog`. They live here rather than in
// the vendored tree because those files are upstream's bytes; each is the same shape as upstream's
// own previews, so `scripts/samples-spec.mjs` reads them alongside the vendored ones.

@GlimmerSamplePreview
@Composable
private fun AlertDialogSamplePreview() {
  GlimmerTheme { AlertDialogSample() }
}

@GlimmerSamplePreview
@Composable
private fun AlertDialogConfirmOnlySamplePreview() {
  GlimmerTheme { AlertDialogConfirmOnlySample() }
}
