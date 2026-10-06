@file:CatalogGroup(name = "Card", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.xr.glimmer.ActionCard
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ImageCard
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant

// The card's slots — image, title, subtitle, leading and trailing icon — are content axes of
// ONE component, so they fold in as variants rather than splitting the card four ways.
//
// ## The base sticker draws the card the kit draws
//
// The kit's `Card` (`416:2700`) is a single symbol whose content differences are hidden LAYERS, and
// the layers it ships turned on are `Show Image`, `Show Entity`, `Show Title`, `Show Subtitle` and
// `Show Body` — a 420x412 content card. A sticker drawing `Card {
// Text(stringResource(R.string.card_content)) }` was an
// 80dp row against it, which is why #381's board reported 98% of pixels differing: two different
// pictures, not a card drawn wrongly. Design-led, so the base is the populated form and the bare
// one is a variant under it.
//
// `Show Image` and `Show Action` are the two layers `Card` itself cannot carry. alpha20 removed
// `Card`'s `header` slot and moved the image onto `ImageCard`, the way the action slot already
// lives
// on `ActionCard`. The symbol the kit publishes is therefore an `ImageCard` call, so the component
// mapped to `416:2700` is `ImageCard` — its base sticker invokes exactly the API that draws the
// kit's populated card, and parity compares like with like. `Card` and `ActionCard` fold in beneath
// it as `content=` variants, the treatment `AGENTS.md` gives every "it is a separate composable"
// axis. `LeadingImageCard` and `TrailingImageCard` place the image beside the content, a layout the
// kit's symbol has no layer for, so they stay declared in `kit-gaps.json`.

@CatalogComponent(
  id = "ImageCard",
  // The kit's `Card` component with its shipped layers on — image, entity, title, subtitle and
  // body. Since alpha20 that is an `ImageCard`: plain `Card` has no image slot.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/416:2700",
  caption = "A card for a unit of content. Image, title, subtitle and the icon slots.",
)
@BuilderComponent(component = "ImageCard", canvas = "glimmer/image-card")
@Preview
@Composable
fun CardSticker() = Sticker {
  ContentFrame {
    ImageCard(
      image = {
        Image(
          HeaderImage,
          stringResource(R.string.cd_header_artwork),
          contentScale = ContentScale.FillWidth,
        )
      },
      title = { Text(glimmerText("title", stringResource(R.string.label_title))) },
      subtitle = { Text(stringResource(R.string.label_subtitle)) },
      leadingIcon = { Icon(Icons.Rounded.AccountCircle, stringResource(R.string.cd_sender)) },
    ) {
      Text(stringResource(R.string.label_body))
    }
  }
}

@CatalogVariant(
  of = "ImageCard",
  props = ["content=no-image"],
  caption = "The kit's card with its image layer off, which is plain `Card`.",
)
@BuilderComponent(component = "Card", canvas = "glimmer/card")
@Preview
@Composable
fun CardNoImageSticker() = Sticker {
  ContentFrame {
    Card(
      title = { Text(glimmerText("title", stringResource(R.string.label_title))) },
      subtitle = { Text(stringResource(R.string.label_subtitle)) },
      leadingIcon = { Icon(Icons.Rounded.AccountCircle, stringResource(R.string.cd_sender)) },
    ) {
      Text(stringResource(R.string.label_body))
    }
  }
}

@CatalogVariant(
  of = "ImageCard",
  props = ["content=text-only"],
  caption = "The bare `Card`: content and nothing else.",
)
@Preview
@Composable
fun CardTextOnlySticker() = Sticker {
  ContentFrame { Card { Text(stringResource(R.string.label_body)) } }
}

@CatalogVariant(
  of = "ImageCard",
  props = ["content=trailing-icon"],
  caption = "`Card` with an icon after the content.",
)
@Preview
@Composable
fun CardTrailingIconSticker() = Sticker {
  ContentFrame {
    Card(
      trailingIcon = { Icon(Icons.Rounded.AccountCircle, stringResource(R.string.cd_sender)) },
      title = { Text(glimmerText("title", stringResource(R.string.label_title))) },
    ) {
      Text(stringResource(R.string.label_body))
    }
  }
}

@CatalogVariant(
  of = "ImageCard",
  props = ["content=action"],
  caption = "The kit's action layer, which Glimmer puts on `ActionCard` rather than on `Card`.",
)
@BuilderComponent(component = "ActionCard", canvas = "glimmer/action-card")
@Preview
@Composable
fun CardActionSticker() = Sticker {
  ContentFrame {
    val c = counted(glimmerText("label", stringResource(R.string.label_button)))
    ActionCard(
      action = { Button(onClick = c.onClick) { Text(c.label) } },
      title = { Text(glimmerText("title", stringResource(R.string.label_title))) },
    ) {
      Text(stringResource(R.string.label_body))
    }
  }
}

// The title chip is a separate component, not a card slot: it labels the content BESIDE it, and
// Glimmer publishes a spacing token (`TitleChipDefaults.associatedContentSpacing`) for the gap
// between the two. A chip drawn inside a card would misstate that relationship.

@CatalogComponent(
  id = "TitleChip",
  // The kit's `Title chip` component, leading-icon slot included.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/5315:4722",
  caption = "Labels the content it sits above. Carries its own leading-icon slot.",
)
@BuilderComponent(component = "TitleChip", canvas = "glimmer/title-chip")
@Preview
@Composable
fun TitleChipSticker() = Sticker {
  // The kit's symbol draws its entity slot filled and labels it "Title Chip" (146x44); the
  // label-only chip is 107x44 and is the variant below.
  TitleChip(
    leadingIcon = { Icon(Icons.Rounded.AccountCircle, stringResource(R.string.cd_sender)) }
  ) {
    Text(glimmerText("label", stringResource(R.string.label_title_chip)))
  }
}

@CatalogVariant(
  of = "TitleChip",
  props = ["content=label-only"],
  caption = "Label with no icon.",
)
@Preview
@Composable
fun TitleChipLabelOnlySticker() = Sticker {
  TitleChip { Text(glimmerText("label", stringResource(R.string.label_title_chip))) }
}
