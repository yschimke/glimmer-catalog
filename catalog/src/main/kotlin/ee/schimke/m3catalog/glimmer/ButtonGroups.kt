@file:CatalogGroup(name = "Button group", section = "Actions")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.ButtonGroup
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.IconToggleButton
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.rememberButtonGroupState
import ee.schimke.composeai.data.overrides.PreviewOverrideOption
import ee.schimke.composeai.overrides.previewOverrideChoice
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction

// ## Every kit cell draws one button focused
//
// The kit's `Button group` set (`40000116:6496`) has three axes, and `Focus=` is the one that
// matters most: every cell draws exactly one item in its `State=Focused` treatment — the base
// cell's first button, `Focus=Middle`'s second, `Focus=End`'s last, scrolled so that item sits in
// the strip. A sticker that never focuses the group draws none of them, which is what the base
// sticker did until now.
//
// The focus is REAL rather than drawn: each cell has the harness focus the item
// (`interactionIndex`), the way the `focused` state cells on every other sticker do, and
// `rememberButtonGroupState(initialItemIndex)` — the public API a caller uses to start a group on
// an item — scrolls the strip to it. A focus request from inside the sticker does not land before
// the capture, which is why the interaction is the harness's.
//
// `Type=Toggle set` is the kit's group of three 48dp icon toggles — it is named `Length=2` in the
// kit but draws three — so it is `IconToggleButton`s inside the same `ButtonGroup`.

@CatalogComponent(
  id = "ButtonGroup",
  // `Type=Button set, Length=3+, Focus=Start` in the kit's `Button group` set
  // (`40000116:6496`). The group owns the initial focus and trailing scrim.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000116:6497",
  caption = "A horizontally scrolling set of buttons that enlarges and centers the focused item.",
)
@BuilderComponent(component = "ButtonGroup", canvas = "glimmer/button-group")
@OverrideVariant(name = "focused", interaction = VariantInteraction.Focused)
@ButtonGroupKitCells
@Preview
@Composable
fun ButtonGroupSticker() = Sticker {
  val type = buttonGroupType()
  val count = if (type == "toggle") 3 else buttonGroupLength()
  val initialIndex =
    when (buttonGroupFocus()) {
      "middle" -> 1
      "end" -> count - 1
      else -> 0
    }.coerceIn(0, count - 1)
  // The kit gives the strip 16dp horizontal insets. alpha20's temporary default is 44dp
  // (b/535205202), but contentPadding is a public caller parameter, so the design-led value belongs
  // here rather than being recorded as an unexplained divergence.
  ButtonGroup(
    modifier = Modifier.width(KitContentWidth),
    state = rememberButtonGroupState(initialItemIndex = initialIndex),
    contentPadding = PaddingValues(horizontal = 16.dp),
  ) {
    repeat(count) {
      if (type == "toggle") {
        var checked by remember { mutableStateOf(false) }
        IconToggleButton(checked = checked, onCheckedChange = { checked = it }) {
          Icon(
            if (checked) Icons.Rounded.Mic else Icons.Rounded.MicOff,
            stringResource(R.string.cd_microphone),
          )
        }
      } else {
        val c = counted(stringResource(R.string.label_button))
        Button(
          modifier = Modifier.width(146.dp),
          onClick = c.onClick,
          leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
        ) {
          Text(c.label)
        }
      }
    }
  }
}

/** The `focus` knob: which item the group starts on, in the kit's `Focus=` vocabulary. */
@Composable
private fun buttonGroupFocus(): String =
  previewOverrideChoice(
    "focus",
    "start",
    listOf(
      PreviewOverrideOption("start", "Start"),
      PreviewOverrideOption("middle", "Middle"),
      PreviewOverrideOption("end", "End"),
    ),
  )

/** The `length` knob: how many buttons the group holds. The kit's `3+` is drawn as three. */
@Composable
private fun buttonGroupLength(): Int =
  when (
    previewOverrideChoice(
      "length",
      "3+",
      listOf(
        PreviewOverrideOption("3+", "3+"),
        PreviewOverrideOption("2", "2"),
        PreviewOverrideOption("1", "1"),
      ),
    )
  ) {
    "2" -> 2
    "1" -> 1
    else -> 3
  }

/** The `type` knob: a set of buttons, or the kit's set of icon toggles. */
@Composable
private fun buttonGroupType(): String =
  previewOverrideChoice(
    "type",
    "button",
    listOf(
      PreviewOverrideOption("button", "Button set"),
      PreviewOverrideOption("toggle", "Toggle set"),
    ),
  )

/** The kit's other eight `Button group` cells, each a seeded crossing of the sticker's knobs. */
@OverrideVariant(
  name = "cell-type-button-set-length-3-focus-middle-40000116-6502",
  strings = ["focus=middle"],
  kitProps = ["Type=Button set", "Length=3+", "Focus=Middle"],
  interaction = VariantInteraction.Focused,
  interactionIndex = 1,
  secondary = true,
)
@OverrideVariant(
  name = "cell-type-button-set-length-3-focus-end-40000116-6508",
  strings = ["focus=end"],
  kitProps = ["Type=Button set", "Length=3+", "Focus=End"],
  interaction = VariantInteraction.Focused,
  interactionIndex = 2,
  secondary = true,
)
@OverrideVariant(
  name = "cell-type-button-set-length-2-focus-start-40000116-6513",
  strings = ["length=2"],
  kitProps = ["Type=Button set", "Length=2", "Focus=Start"],
  interaction = VariantInteraction.Focused,
  secondary = true,
)
@OverrideVariant(
  name = "cell-type-button-set-length-2-focus-end-40000116-6521",
  strings = ["length=2", "focus=end"],
  kitProps = ["Type=Button set", "Length=2", "Focus=End"],
  interaction = VariantInteraction.Focused,
  interactionIndex = 1,
  secondary = true,
)
@OverrideVariant(
  name = "cell-type-button-set-length-1-focus-start-40000116-6528",
  strings = ["length=1"],
  kitProps = ["Type=Button set", "Length=1", "Focus=Start"],
  interaction = VariantInteraction.Focused,
  secondary = true,
)
@OverrideVariant(
  name = "cell-type-toggle-set-length-2-focus-start-40000116-6535",
  strings = ["type=toggle"],
  kitProps = ["Type=Toggle set", "Length=2", "Focus=Start"],
  interaction = VariantInteraction.Focused,
  secondary = true,
)
@OverrideVariant(
  name = "cell-type-toggle-set-length-2-focus-middle-40000116-6539",
  strings = ["type=toggle", "focus=middle"],
  kitProps = ["Type=Toggle set", "Length=2", "Focus=Middle"],
  interaction = VariantInteraction.Focused,
  interactionIndex = 1,
  secondary = true,
)
@OverrideVariant(
  name = "cell-type-toggle-set-length-2-focus-end-40000116-6543",
  strings = ["type=toggle", "focus=end"],
  kitProps = ["Type=Toggle set", "Length=2", "Focus=End"],
  interaction = VariantInteraction.Focused,
  interactionIndex = 2,
  secondary = true,
)
annotation class ButtonGroupKitCells
