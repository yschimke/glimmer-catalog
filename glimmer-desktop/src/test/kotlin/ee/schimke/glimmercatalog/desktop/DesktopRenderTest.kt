package ee.schimke.glimmercatalog.desktop

import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import java.io.File
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class DesktopRenderTest {
  @Test
  fun `vendored Glimmer renders on desktop without Android or Robolectric`() = runComposeUiTest {
    setContent {
      GlimmerTheme {
        Button(onClick = {}, modifier = androidx.compose.ui.Modifier.testTag("button")) {
          Text("Glimmer on Compose Desktop")
        }
      }
    }

    onNodeWithText("Glimmer on Compose Desktop").assertExists()
    val image = onNodeWithTag("button").captureToImage().toAwtImage()
    assertTrue(image.width > 40 && image.height > 20)
    val colours = HashSet<Int>()
    for (y in 0 until image.height step 2) {
      for (x in 0 until image.width step 2) colours += image.getRGB(x, y)
    }
    assertTrue(colours.size > 8, "expected a drawn button, saw ${colours.size} colours")

    val out = File(System.getProperty("glimmer.desktop.out") ?: "build/glimmer-desktop")
    out.mkdirs()
    assertTrue(ImageIO.write(image, "png", File(out, "cmp-desktop-render.png")))
  }
}
