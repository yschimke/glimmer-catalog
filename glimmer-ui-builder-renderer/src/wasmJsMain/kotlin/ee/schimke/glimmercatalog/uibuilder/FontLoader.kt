@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package ee.schimke.glimmercatalog.uibuilder

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

internal fun loadRoboto(onLoaded: (FontFamily) -> Unit, onFailed: (String) -> Unit) {
  fetchFonts(
    onLoaded = { regular, medium ->
      onLoaded(
        FontFamily(
          Font("Roboto-Regular", regular.toByteArray(), FontWeight.Normal),
          Font("Roboto-Medium", medium.toByteArray(), FontWeight.Medium),
        )
      )
    },
    onFailed = { onFailed(it.toString()) },
  )
}

private fun JsAny.toByteArray(): ByteArray =
  ByteArray(byteLength(this)) { byteAt(this, it).toByte() }

@JsFun(
  """(onLoaded, onFailed) => {
    const load = (file) => fetch('fonts/' + file).then((response) => {
      if (!response.ok) throw new Error(file + ': HTTP ' + response.status);
      return response.arrayBuffer();
    }).then((buffer) => new Int8Array(buffer));
    Promise.all([load('Roboto-Regular.ttf'), load('Roboto-Medium.ttf')])
      .then(([regular, medium]) => onLoaded(regular, medium), (error) => onFailed(String(error)));
  }"""
)
private external fun fetchFonts(onLoaded: (JsAny, JsAny) -> Unit, onFailed: (JsString) -> Unit)

@JsFun("(array) => array.length") private external fun byteLength(array: JsAny): Int

@JsFun("(array, index) => array[index]") private external fun byteAt(array: JsAny, index: Int): Int
