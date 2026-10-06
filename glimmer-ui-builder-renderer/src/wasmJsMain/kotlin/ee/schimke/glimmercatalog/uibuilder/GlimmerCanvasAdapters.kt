package ee.schimke.glimmercatalog.uibuilder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.ActionCard
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.ButtonGroup
import androidx.xr.glimmer.ButtonSize
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.ContainedVoiceInputIndicator
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.IconButton
import androidx.xr.glimmer.IconToggleButton
import androidx.xr.glimmer.ImageCard
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.ToggleButton
import androidx.xr.glimmer.VoiceInputIndicator
import androidx.xr.glimmer.list.GlimmerLazyColumn
import androidx.xr.glimmer.stack.VerticalStack
import ee.schimke.composeai.uibuilder.renderer.sdk.canvasAdapterRegistry
import ee.schimke.composeai.uibuilder.renderer.sdk.googleMaterialIconImageVector

internal val glimmerCanvasAdapters = canvasAdapterRegistry {
  register("frame/rect") {
    val canvas = this
    Box(modifier.fillMaxSize().background(Color.Black)) { canvas.Slot("children") }
  }
  register("layout/box") {
    val canvas = this
    Box(modifier) { canvas.Slot("children") }
  }
  register("layout/column") {
    val canvas = this
    Column(modifier) { canvas.Slot("children") }
  }
  register("layout/row") {
    val canvas = this
    Row(modifier) { canvas.Slot("children") }
  }
  register("layout/spacer") { Spacer(modifier) }

  register("glimmer/button-group") {
    val canvas = this
    ButtonGroup(modifier = modifier) { canvas.Slot("content") }
  }
  register("glimmer/button") {
    val canvas = this
    Button(
      onClick = { dispatch("onClick") },
      modifier = modifier,
      enabled = boolean("enabled", true),
      buttonSize = buttonSize(),
      leadingIcon = optionalSlot("leadingIcon"),
      trailingIcon = optionalSlot("trailingIcon"),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/toggle-button") {
    val canvas = this
    val checked = boolean("checked")
    ToggleButton(
      checked = checked,
      onCheckedChange = {
        updateBoundState("checked", it.toString())
        dispatch("onCheckedChange")
      },
      modifier = modifier,
      enabled = boolean("enabled", true),
      buttonSize = buttonSize(),
      leadingIcon = optionalSlot("leadingIcon"),
      trailingIcon = optionalSlot("trailingIcon"),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/icon-button") {
    val canvas = this
    IconButton(
      onClick = { dispatch("onClick") },
      modifier = modifier,
      enabled = boolean("enabled", true),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/icon-toggle-button") {
    val canvas = this
    val checked = boolean("checked")
    IconToggleButton(
      checked = checked,
      onCheckedChange = {
        updateBoundState("checked", it.toString())
        dispatch("onCheckedChange")
      },
      modifier = modifier,
      enabled = boolean("enabled", true),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/card") {
    val canvas = this
    Card(
      modifier = modifier,
      title = optionalSlot("title"),
      subtitle = optionalSlot("subtitle"),
      leadingIcon = optionalSlot("leadingIcon"),
      trailingIcon = optionalSlot("trailingIcon"),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/action-card") {
    val canvas = this
    ActionCard(
      action = { canvas.Slot("action") },
      modifier = modifier,
      title = optionalSlot("title"),
      subtitle = optionalSlot("subtitle"),
      leadingIcon = optionalSlot("leadingIcon"),
      trailingIcon = optionalSlot("trailingIcon"),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/image-card") {
    val canvas = this
    ImageCard(
      image = { canvas.Slot("image") },
      modifier = modifier,
      title = optionalSlot("title"),
      subtitle = optionalSlot("subtitle"),
      leadingIcon = optionalSlot("leadingIcon"),
      trailingIcon = optionalSlot("trailingIcon"),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/list-item") {
    val canvas = this
    ListItem(
      modifier = modifier,
      supportingLabel = optionalSlot("supportingLabel"),
      leadingIcon = optionalSlot("leadingIcon"),
      trailingIcon = optionalSlot("trailingIcon"),
    ) {
      canvas.Slot("content")
    }
  }
  register("glimmer/title-chip") {
    val canvas = this
    TitleChip(modifier = modifier, leadingIcon = optionalSlot("leadingIcon")) {
      canvas.Slot("content")
    }
  }
  register("glimmer/text") {
    Text(
      text = string("text", "Text"),
      modifier = modifier,
      softWrap = boolean("softWrap", true),
      maxLines = integer("maxLines", Int.MAX_VALUE).coerceAtLeast(1),
      minLines = integer("minLines", 1).coerceAtLeast(1),
      onTextLayout = ::recordTextLayout,
    )
  }
  register("glimmer/icon") {
    val vector = googleMaterialIconImageVector(string("iconKey", "star"))
    if (vector == null) {
      Box(modifier.size(float("sizeDp", 24f).dp), contentAlignment = Alignment.Center) { Text("?") }
    } else {
      Icon(
        imageVector = vector,
        contentDescription = string("contentDescription").ifEmpty { null },
        modifier = modifier.size(float("sizeDp", 24f).dp),
      )
    }
  }
  register("glimmer/voice-input-indicator") {
    VoiceInputIndicator(level = { float("level", 0.8f) }, modifier = modifier)
  }
  register("glimmer/contained-voice-input-indicator") {
    ContainedVoiceInputIndicator(level = { float("level", 0.8f) }, modifier = modifier)
  }
  register("glimmer/glimmer-lazy-column") {
    val canvas = this
    GlimmerLazyColumn(
      modifier = modifier,
      userScrollEnabled = boolean("userScrollEnabled", true),
      reverseLayout = boolean("reverseLayout"),
    ) {
      repeat(canvas.itemCount("content")) { index -> item { canvas.Item("content", index) } }
    }
  }
  register("glimmer/vertical-stack") {
    val canvas = this
    VerticalStack(modifier = modifier) {
      repeat(canvas.itemCount("content")) { index -> item { canvas.Item("content", index) } }
    }
  }
}

private fun ee.schimke.composeai.uibuilder.renderer.sdk.CanvasNodeScope.buttonSize(): ButtonSize =
  if (string("buttonSize", string("size")).equals("large", ignoreCase = true)) ButtonSize.Large
  else ButtonSize.Medium

@androidx.compose.runtime.Composable
private fun ee.schimke.composeai.uibuilder.renderer.sdk.CanvasNodeScope.optionalSlot(
  name: String
): (@androidx.compose.runtime.Composable () -> Unit)? {
  if (itemCount(name) == 0) return null
  return { Slot(name) }
}
