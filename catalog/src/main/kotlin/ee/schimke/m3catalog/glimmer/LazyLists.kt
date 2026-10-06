@file:CatalogGroup(name = "Lazy list", section = "Containment")

package ee.schimke.m3catalog.glimmer

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.list.GlimmerLazyColumn
import androidx.xr.glimmer.list.GlimmerLazyListState
import ee.schimke.composeai.data.overrides.PreviewOverrideOption
import ee.schimke.composeai.overrides.previewOverrideChoice
import ee.schimke.composeai.preview.BuilderComponent
import ee.schimke.composeai.preview.CatalogComponent
import ee.schimke.composeai.preview.CatalogGroup
import ee.schimke.composeai.preview.CatalogVariant
import ee.schimke.composeai.preview.OverrideVariant
import ee.schimke.composeai.preview.VariantInteraction
import kotlinx.coroutines.flow.first

// The rows are copy rather than sample data — they are words a reader reads — so they resolve from
// resources like everything else this module draws. Held as id lists so the row count stays one
// number rather than three.
private val GroceryLabels =
  listOf(R.string.item_milk, R.string.item_bread, R.string.item_spinach, R.string.item_cereal)
private val MeetingLabels =
  listOf(
    R.string.meeting_design_check_in,
    R.string.meeting_one_to_one,
    R.string.meeting_lunch,
    R.string.meeting_review,
  )
private val MeetingTimes =
  listOf(
    R.string.time_early_morning,
    R.string.time_late_morning,
    R.string.time_midday,
    R.string.time_afternoon,
  )

// ## `List index=` is where the focus sits
//
// Every cell of the kit's `1-line list` and `2-line list` sets draws one row in its focused
// treatment and scrolls the column to keep it in view: `List index=Top` focuses the first row,
// `Mid` the second, `Bottom` the last. The exact cells below drive REAL focus onto that row through
// the harness, and an `index` knob scrolls the column so its autofocus line rests on that row —
// Glimmer focuses whichever row sits under the line, so harness focus on a later row would be
// pulled
// straight back. The 2-line set's `Top` cells draw no focused row at all, so its base sticker stays
// unfocused and only `Mid` and `Bottom` carry an interaction.
//
// Three `Bottom` cells are not authored yet: `Title=True` on either set and the 2-line set's
// `Title=False`. Scrolled to the end, those columns still hold their last row under the edge scrim
// with the focus line between rows when the capture is taken, and the kit's 2-line column has three
// rows where this one has four. A capture that misses the cell would be worse than no cell.

@CatalogComponent(
  id = "GlimmerLazyColumn",
  // `List index=Top, Title=False` in the kit's `1-line list` set (`4116:5211`).
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/4116:5212",
  caption = "A vertically scrolling Glimmer list with edge scrims and focus-aware snapping.",
)
@BuilderComponent(component = "GlimmerLazyColumn", canvas = "glimmer/glimmer-lazy-column")
// The kit draws its base cell with the first item focused, which only the harness can capture.
// Naming the cell's properties makes this render, not the unfocused base, its parity pair
// (design-parity kit-index 1.6.0 lets an explicit kitProps variant claim its cell).
@OverrideVariant(
  name = "focused",
  interaction = VariantInteraction.Focused,
  kitProps = ["List index=Top", "Title=False"],
)
@OverrideVariant(
  name = "cell-list-index-mid-title-false-4116-5221",
  strings = ["index=mid"],
  interaction = VariantInteraction.Focused,
  kitProps = ["List index=Mid", "Title=False"],
  secondary = true,
)
@OverrideVariant(
  name = "cell-list-index-bottom-title-false-40000115-2000",
  strings = ["index=bottom"],
  interaction = VariantInteraction.Focused,
  kitProps = ["List index=Bottom", "Title=False"],
  secondary = true,
)
@Preview
@Composable
fun GlimmerLazyColumnSticker() = Sticker {
  GlimmerLazyColumn(
    modifier = Modifier.width(KitContentWidth).height(364.dp),
    state = rememberListAtIndex(GroceryLabels.size),
  ) {
    items(count = GroceryLabels.size) { index -> GroceryListItem(index) }
  }
}

@CatalogVariant(
  of = "GlimmerLazyColumn",
  props = ["Title=True"],
  caption = "Keeps a title chip above the scrolling items.",
)
@BuilderComponent(component = "GlimmerLazyColumn", canvas = "glimmer/glimmer-lazy-column")
// The kit draws its base cell with the first item focused, which only the harness can capture.
// Naming the cell's properties makes this render, not the unfocused base, its parity pair
// (design-parity kit-index 1.6.0 lets an explicit kitProps variant claim its cell).
@OverrideVariant(
  name = "focused",
  interaction = VariantInteraction.Focused,
  kitProps = ["List index=Top", "Title=True"],
)
@OverrideVariant(
  name = "cell-list-index-mid-title-true-40000115-1924",
  strings = ["index=mid"],
  interaction = VariantInteraction.Focused,
  kitProps = ["List index=Mid", "Title=True"],
  secondary = true,
)
@Preview
@Composable
fun GlimmerLazyColumnWithTitleSticker() = Sticker {
  GlimmerLazyColumn(
    title = { TitleChip { Text(stringResource(R.string.list_title_ingredients)) } },
    modifier = Modifier.width(KitContentWidth).height(364.dp),
    state = rememberListAtIndex(GroceryLabels.size),
  ) {
    items(count = GroceryLabels.size) { index -> GroceryListItem(index) }
  }
}

@CatalogComponent(
  id = "GlimmerLazyColumn/TwoLine",
  // The kit makes its item-content choice a separate set. This remains the same named Compose API,
  // with the supporting-label slot on each real ListItem supplying the second line.
  reference = "figma:HKfLClZDLRyMhf4IQQLna8/4116:5251",
  caption = "The lazy column populated with two-line list items.",
)
@OverrideVariant(
  name = "cell-list-index-mid-title-false-4116-5259",
  strings = ["index=mid"],
  interaction = VariantInteraction.Focused,
  kitProps = ["List index=Mid", "Title=False"],
  secondary = true,
)
@OverrideVariant(
  name = "cell-list-index-top-title-true-40000116-2394",
  strings = ["title=true"],
  kitProps = ["List index=Top", "Title=True"],
  secondary = true,
)
@OverrideVariant(
  name = "cell-list-index-mid-title-true-40000116-2458",
  strings = ["title=true", "index=mid"],
  interaction = VariantInteraction.Focused,
  kitProps = ["List index=Mid", "Title=True"],
  secondary = true,
)
@Preview
@Composable
fun GlimmerLazyColumnTwoLineSticker() = Sticker {
  val modifier = Modifier.width(KitContentWidth).height(364.dp)
  val state = rememberListAtIndex(MeetingLabels.size)
  if (twoLineTitle()) {
    GlimmerLazyColumn(
      title = { TitleChip { Text(stringResource(R.string.label_title)) } },
      modifier = modifier,
      state = state,
    ) {
      items(count = MeetingLabels.size) { index -> MeetingListItem(index) }
    }
  } else {
    GlimmerLazyColumn(modifier = modifier, state = state) {
      items(count = MeetingLabels.size) { index -> MeetingListItem(index) }
    }
  }
}

@Composable
private fun MeetingListItem(index: Int) {
  val c = counted(stringResource(MeetingLabels[index]))
  ListItem(
    onClick = c.onClick,
    supportingLabel = { Text(localizedDigits(stringResource(MeetingTimes[index]))) },
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(c.label)
  }
}

@Composable
private fun GroceryListItem(index: Int) {
  val c = counted(stringResource(GroceryLabels[index]))
  ListItem(
    onClick = c.onClick,
    leadingIcon = { Icon(Icons.AutoMirrored.Rounded.Send, stringResource(R.string.cd_send)) },
  ) {
    Text(c.label)
  }
}

/** The `title` knob on the two-line column: the kit's `Title=` axis, off by default. */
@Composable
private fun twoLineTitle(): Boolean =
  previewOverrideChoice(
    "title",
    "false",
    listOf(PreviewOverrideOption("false", "No title"), PreviewOverrideOption("true", "Title")),
  ) == "true"

/**
 * A list state whose focus line starts on the [listIndex] row.
 *
 * Not `initialFirstVisibleItemIndex`: that positions CONTENT, and a column barely taller than its
 * viewport clamps every non-zero index to the end. Glimmer's autofocus focuses whichever row sits
 * under its focus line, and `scrollBy` moves the user scroll that line follows, so the row is
 * reached by scrolling the distance between the first row and it.
 */
@Composable
private fun rememberListAtIndex(count: Int): GlimmerLazyListState {
  val index = listIndex(count)
  // A fresh state per index, so a live change of the knob always measures from the top.
  val state = remember(index) { GlimmerLazyListState() }
  LaunchedEffect(state, index) {
    when (index) {
      0 -> {}
      // The last row: scroll to the end, where the focus line rests on it.
      count - 1 -> state.scrollBy(LIST_END_SCROLL)
      // Aim the focus line at the middle of the row rather than its top edge: with a title the line
      // starts higher than the first row, and a row-edge target leaves it short.
      else -> {
        val items = snapshotFlow { state.layoutInfo.visibleItemsInfo }.first { it.size > index }
        state.scrollBy((items[index].offset - items[0].offset + items[index].size / 2).toFloat())
      }
    }
  }
  return state
}

/** Further than any of these columns can scroll; `scrollBy` stops at the end. */
private const val LIST_END_SCROLL = 100_000f

/**
 * The `index` knob: where the column starts, in the kit's `List index=` vocabulary. Top is the
 * first row, Mid the second and Bottom the last; the column's autofocus puts focus on that row.
 */
@Composable
private fun listIndex(count: Int): Int =
  when (
    previewOverrideChoice(
      "index",
      "top",
      listOf(
        PreviewOverrideOption("top", "Top"),
        PreviewOverrideOption("mid", "Mid"),
        PreviewOverrideOption("bottom", "Bottom"),
      ),
    )
  ) {
    "mid" -> 1
    "bottom" -> count - 1
    else -> 0
  }
