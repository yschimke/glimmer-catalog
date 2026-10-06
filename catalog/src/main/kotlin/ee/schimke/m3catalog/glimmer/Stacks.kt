@file:CatalogGroup(name = "Stack", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.CardDefaults
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.stack.VerticalStack
import ee.schimke.composeai.data.overrides.PreviewOverrideOption
import ee.schimke.composeai.overrides.previewOverrideChoice
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.OverrideVariant

@CatalogComponent(
  id = "VerticalStack",
  // `Type=Cards` in the kit's `Stack` set (`40000042:4077`). The other published cell,
  // `Type=Home`, is the same API stacking title chips, seeded by the `type` knob below.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/40000042:4078",
  caption =
    "Layers focusable content in depth, scaling and revealing the items behind the front one.",
)
@BuilderComponent(component = "VerticalStack", canvas = "glimmer/vertical-stack")
@OverrideVariant(
  name = "cell-type-home-40000042-4081",
  strings = ["type=home"],
  kitProps = ["Type=Home"],
  secondary = true,
)
@Preview
@Composable
fun VerticalStackSticker() = Sticker { if (stackType() == "home") HomeStack() else CardStack() }

/** `Type=Cards`: two content cards, the front one carrying title, subtitle and body. */
@Composable
private fun CardStack() {
  VerticalStack(modifier = Modifier.width(KitContentWidth).height(178.dp)) {
    item(key = "front") {
      Card(
        modifier = Modifier.fillMaxSize().itemDecoration(CardDefaults.shape),
        title = { Text(stringResource(R.string.label_title)) },
        subtitle = { Text(stringResource(R.string.label_subtitle)) },
      ) {
        Text(stringResource(R.string.stack_card_body))
      }
    }
    item(key = "back") {
      Card(modifier = Modifier.fillMaxSize().itemDecoration(CardDefaults.shape)) {
        Text(stringResource(R.string.label_body))
      }
    }
  }
}

/**
 * `Type=Home`: the kit's home-screen stack, a pile of title chips — an avatar and a name — with the
 * front chip on top. The same `VerticalStack` API as the card stack, sized to the chip.
 */
@Composable
private fun HomeStack() {
  VerticalStack(modifier = Modifier.width(HomeChipWidth).height(HomeStackHeight)) {
    repeat(2) { index ->
      item(key = "chip-$index") {
        TitleChip(
          modifier = Modifier.itemDecoration(CircleShape),
          leadingIcon = { Icon(Icons.Rounded.AccountCircle, stringResource(R.string.cd_sender)) },
        ) {
          Text(stringResource(R.string.label_title_chip))
        }
      }
    }
  }
}

private val HomeChipWidth = 160.dp
private val HomeStackHeight = 56.dp

/** The `type` knob: the kit's `Type=` axis on the `Stack` set. */
@Composable
private fun stackType(): String =
  previewOverrideChoice(
    "type",
    "cards",
    listOf(PreviewOverrideOption("cards", "Cards"), PreviewOverrideOption("home", "Home")),
  )
