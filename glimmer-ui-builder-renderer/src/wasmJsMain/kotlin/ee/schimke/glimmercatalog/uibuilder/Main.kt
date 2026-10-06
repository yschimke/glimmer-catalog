package ee.schimke.glimmercatalog.uibuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.Typography
import ee.schimke.composeai.uibuilder.protocol.UiBuilderRendererSurfaceModeV2
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasDocumentHost
import ee.schimke.composeai.uibuilder.renderer.sdk.CanvasMode
import ee.schimke.composeai.uibuilder.renderer.sdk.RenderCanvasNode
import ee.schimke.composeai.uibuilder.renderer.sdk.UiBuilderSemanticActionController
import ee.schimke.composeai.uibuilder.renderer.sdk.applyCanvasModifier
import ee.schimke.composeai.uibuilder.renderer.sdk.startCatalogRenderer
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import org.jetbrains.skiko.InternalSkikoApi
import org.jetbrains.skiko.wasm.awaitSkiko

private val componentIds =
  setOf(
    "frame/rect",
    "layout/box",
    "layout/column",
    "layout/row",
    "layout/spacer",
    "glimmer/action-card",
    "glimmer/button",
    "glimmer/button-group",
    "glimmer/card",
    "glimmer/contained-voice-input-indicator",
    "glimmer/glimmer-lazy-column",
    "glimmer/icon",
    "glimmer/icon-button",
    "glimmer/icon-toggle-button",
    "glimmer/image-card",
    "glimmer/list-item",
    "glimmer/text",
    "glimmer/title-chip",
    "glimmer/toggle-button",
    "glimmer/vertical-stack",
    "glimmer/voice-input-indicator",
  )

@OptIn(InternalSkikoApi::class)
fun main() {
  val actions = UiBuilderSemanticActionController()
  awaitSkiko.then(
    onFulfilled = {
      loadRoboto(
        onLoaded = { family -> start(actions, family) },
        onFailed = { error("the renderer's fonts did not load: $it") },
      )
      null
    },
    onRejected = { error("Skiko initialization failed: $it") },
  )
}

private fun start(
  actions: UiBuilderSemanticActionController,
  family: androidx.compose.ui.text.font.FontFamily,
) {
  startCatalogRenderer(actions) { document, surface, renderSessionId, onInspectionSnapshot ->
    val hostDensity = LocalDensity.current
    val density =
      Density(
        density = surface.density,
        fontScale =
          document.environment["fontScale"]
            ?.let { it as? JsonPrimitive }
            ?.contentOrNull
            ?.toFloatOrNull()
            ?.takeIf { it.isFinite() && it > 0f } ?: hostDensity.fontScale,
      )
    val mode =
      if (surface.mode == UiBuilderRendererSurfaceModeV2.AUTHORING_UNROLLED)
        CanvasMode.AuthoringUnrolled
      else CanvasMode.Device
    val layoutDirection =
      if ((document.environment["layoutDirection"] as? JsonPrimitive)?.contentOrNull == "rtl")
        LayoutDirection.Rtl
      else LayoutDirection.Ltr
    CompositionLocalProvider(
      LocalDensity provides density,
      LocalLayoutDirection provides layoutDirection,
    ) {
      GlimmerTheme(typography = Typography(defaultFontFamily = family)) {
        Box(
          Modifier.requiredSize(surface.widthDp.dp, surface.heightDp.dp)
            .background(GlimmerTheme.colors.background)
        ) {
          CanvasDocumentHost(
            document = document,
            adapterIds = componentIds.associateWith { it },
            adapterMappings = emptyMap(),
            mode = mode,
            density = density,
            modifier = Modifier.fillMaxSize(),
            renderSessionId = renderSessionId,
            runtimeActionController = actions,
            onInspectionSnapshot = onInspectionSnapshot,
          ) { entry, rootModifier ->
            RenderCanvasNode(
              entry = entry,
              registry = glimmerCanvasAdapters,
              modifier = rootModifier,
              applyModifier = { current, value ->
                current.applyCanvasModifier(
                  value = value,
                  mode = mode,
                  unrolledHorizontally = unrolledHorizontally,
                  resolveColor = ::resolveGlimmerColor,
                  resolveShape = ::resolveGlimmerShape,
                )
              },
              missingComponent = { label, next -> UnsupportedComponent(label, next) },
            ) {
              UnsupportedComponent(node.componentId, prepared.modifier)
            }
          }
        }
      }
    }
  }
}

@androidx.compose.runtime.Composable
private fun resolveGlimmerColor(value: String): Color =
  when (value) {
    "background" -> GlimmerTheme.colors.background
    "surface" -> GlimmerTheme.colors.surface
    "primary" -> GlimmerTheme.colors.primary
    "secondary" -> GlimmerTheme.colors.secondary
    "positive" -> GlimmerTheme.colors.positive
    "negative" -> GlimmerTheme.colors.negative
    "transparent" -> Color.Transparent
    else -> if (value.startsWith("#")) Color(parseArgb(value)) else Color.Unspecified
  }

@androidx.compose.runtime.Composable
private fun resolveGlimmerShape(value: String?): Shape =
  when (value) {
    "large" -> GlimmerTheme.shapes.large
    "medium" -> GlimmerTheme.shapes.medium
    "small" -> GlimmerTheme.shapes.small
    else -> RoundedCornerShape(value?.toFloatOrNull()?.dp ?: 0.dp)
  }

@androidx.compose.runtime.Composable
private fun UnsupportedComponent(label: String, modifier: Modifier) {
  Box(modifier.background(GlimmerTheme.colors.negative)) { Text(label) }
}

private fun parseArgb(value: String): Long {
  val hex = value.removePrefix("#")
  return when (hex.length) {
    6 -> ("FF$hex").toLong(16)
    8 -> hex.toLong(16)
    else -> 0L
  }
}
