package tomeko.entitycrosshair.config

//? if >= 26.1 {
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Visualizer
import org.polyfrost.oneconfig.api.config.v1.annotations.Option
import tomeko.entitycrosshair.utils.copyToClipboard
import tomeko.entitycrosshair.utils.getImageFromClipboard
import tomeko.entitycrosshair.utils.toBase64
import tomeko.entitycrosshair.utils.toBufferedImage
import java.awt.image.BufferedImage

@Option(display = CrosshairEditorVisualizer::class)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CrosshairEditor(
    val category: String = "General",
    val entityMode: Boolean = false,
)

class CrosshairEditorVisualizer : Visualizer {
    @Composable
    override fun visualize(prop: Property<*>) {
        val entityMode = prop.id == "entityCrosshairJson"
        val defaultImg = if (entityMode) DefaultCrosshairs.ENTITY else DefaultCrosshairs.GENERAL

        var setData by remember(prop.id) {
            mutableStateOf(CrosshairSetData.decode(prop.get()?.toString() ?: "", defaultImg))
        }

        val pixels = remember(prop.id) { mutableStateOf(loadPixelsFromBase64(setData.current.img)) }
        var penColor by remember(prop.id) { mutableStateOf(Color.White) }
        var canvasSize by remember(prop.id) { mutableStateOf(setData.canvasSize.coerceIn(15, 32)) }

        fun persist(newSetData: CrosshairSetData) {
            setData = newSetData

            @Suppress("UNCHECKED_CAST")
            (prop as Property<Any>).set(newSetData.encode())

            if (entityMode) EntityCrosshairConfig.onEntityChanged(newSetData) else EntityCrosshairConfig.onGeneralChanged(newSetData)
        }

        fun renderPixelsToImage(size: Int): BufferedImage {
            val img = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    img.setRGB(x, y, pixels.value[x + y * 32] ?: 0)
                }
            }
            return img
        }

        fun saveCurrent() {
            val image = renderPixelsToImage(canvasSize)
            val base64 = toBase64(image)

            val existing = setData.presets.find { it.img == base64 }
            val newCurrent = setData.current.copy(img = base64)
            val newPresets = if (existing != null) {
                setData.presets.toMutableList()
            } else {
                (setData.presets + newCurrent.copy()).toMutableList()
            }

            persist(setData.copy(current = newCurrent, presets = newPresets, canvasSize = canvasSize))
        }

        fun loadImageIntoEditor(image: BufferedImage) {
            if (image.width != image.height || image.width !in 15..32) {
                return
            }
            canvasSize = image.height
            val newPixels = HashMap<Int, Int>()
            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val c = image.getRGB(x, y)
                    if (c ushr 24 != 0) newPixels[x + y * 32] = c
                }
            }
            pixels.value = newPixels
        }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(
                text = if (entityMode) "Entity crosshair" else "Default crosshair",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text("Canvas size: $canvasSize", color = Color.White, modifier = Modifier.width(140.dp))
                Slider(
                    value = canvasSize.toFloat(),
                    onValueChange = { canvasSize = it.toInt().coerceIn(15, 32) },
                    valueRange = 15f..32f,
                    steps = 32 - 15 - 1,
                    modifier = Modifier.weight(1f),
                )
            }

            Row(verticalAlignment = Alignment.Top) {
                val cellPx = 256f / canvasSize
                Canvas(
                    modifier = Modifier
                        .size(256.dp)
                        .pointerInput(canvasSize, penColor) {
                            fun paintAt(offset: Offset, erase: Boolean) {
                                val gx = (offset.x / cellPx).toInt()
                                val gy = (offset.y / cellPx).toInt()
                                if (gx !in 0 until canvasSize || gy !in 0 until canvasSize) return
                                val idx = gx + gy * 32
                                val newPixels = HashMap(pixels.value)
                                if (erase) newPixels.remove(idx) else newPixels[idx] = penColor.toArgb()
                                pixels.value = newPixels
                            }

                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    val pos = event.changes.firstOrNull()?.position ?: continue
                                    when (event.type) {
                                        PointerEventType.Press, PointerEventType.Move -> {
                                            val buttons = event.buttons
                                            if (buttons.isPrimaryPressed) paintAt(pos, erase = false)
                                            else if (buttons.isSecondaryPressed) paintAt(pos, erase = true)
                                        }
                                        else -> {}
                                    }
                                }
                            }
                        },
                ) {
                    drawGrid(canvasSize, cellPx, pixels.value)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text("Pen color", color = Color.White)
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        for (swatch in listOf(Color.White, Color.Black, Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta)) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(1.dp)
                                    .background(swatch)
                                    .pointerInput(Unit) { detectTapGestures { penColor = swatch } },
                            )
                        }
                    }
                    var hex by remember(prop.id) { mutableStateOf(colorToHex(penColor)) }
                    OutlinedTextField(
                        value = hex,
                        onValueChange = {
                            hex = it
                            hexToColor(it)?.let { c -> penColor = c }
                        },
                        label = { Text("Hex (AARRGGBB)") },
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            cursorColor = Color.White,
                            focusedBorderColor = Color.LightGray,
                            unfocusedBorderColor = Color.DarkGray,
                        ),
                        modifier = Modifier.width(160.dp).padding(top = 8.dp),
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(onClick = { pixels.value = emptyMap() }, modifier = Modifier.padding(bottom = 4.dp)) {
                        Text("Reset")
                    }
                    Button(onClick = { saveCurrent() }, modifier = Modifier.padding(bottom = 4.dp)) {
                        Text("Save")
                    }
                    Button(
                        onClick = { getImageFromClipboard()?.let(::loadImageIntoEditor) },
                        modifier = Modifier.padding(bottom = 4.dp),
                    ) {
                        Text("Import from clipboard")
                    }
                    Button(onClick = { copyToClipboard(renderPixelsToImage(canvasSize)) }) {
                        Text("Export / copy")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Saved presets (click to load):", color = Color.White, modifier = Modifier.padding(bottom = 4.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(6), modifier = Modifier.height(160.dp)) {
                items(setData.presets) { preset ->
                    Column(
                        modifier = Modifier.padding(4.dp).pointerInput(preset) {
                            detectTapGestures {
                                setData = setData.copy(current = preset.copy())
                                pixels.value = loadPixelsFromBase64(preset.img)
                                toBufferedImage(preset.img)?.let { canvasSize = it.height }
                            }
                        },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        base64ToImageBitmap(preset.img)?.let { bmp ->
                            androidx.compose.foundation.Image(
                                bitmap = bmp,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                        Button(
                            onClick = {
                                persist(setData.copy(presets = setData.presets.filterNot { it === preset }.toMutableList()))
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                            modifier = Modifier.padding(top = 2.dp),
                        ) {
                            Text("x", color = Color.White, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawGrid(canvasSize: Int, cellPx: Float, pixels: Map<Int, Int>) {
    for (y in 0 until canvasSize) {
        for (x in 0 until canvasSize) {
            val idx = x + y * 32
            val argb = pixels[idx]
            val checker = if ((x + y) % 2 == 0) Color(0xFF3A3F4B) else Color(0xFF2E323C)
            val color = if (argb != null) argbToComposeColor(argb) else checker
            drawRect(
                color = color,
                topLeft = Offset(x * cellPx, y * cellPx),
                size = androidx.compose.ui.geometry.Size(cellPx - 1f, cellPx - 1f),
            )
        }
    }
}

private fun loadPixelsFromBase64(base64: String): Map<Int, Int> {
    val image = toBufferedImage(base64) ?: return emptyMap()
    val map = HashMap<Int, Int>()
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val c = image.getRGB(x, y)
            if (c ushr 24 != 0) map[x + y * 32] = c
        }
    }
    return map
}

private fun base64ToImageBitmap(base64: String): ImageBitmap? {
    val bytes = try {
        java.util.Base64.getDecoder().decode(base64)
    } catch (_: Exception) {
        return null
    }
    return try {
        org.jetbrains.skia.Image.makeFromEncoded(bytes).toComposeImageBitmap()
    } catch (_: Exception) {
        null
    }
}

private fun Color.toArgb(): Int {
    val a = (alpha * 255f).toInt() and 0xFF
    val r = (red * 255f).toInt() and 0xFF
    val g = (green * 255f).toInt() and 0xFF
    val b = (blue * 255f).toInt() and 0xFF
    return (a shl 24) or (r shl 16) or (g shl 8) or b
}

private fun argbToComposeColor(argb: Int): Color {
    val a = (argb ushr 24 and 0xFF) / 255f
    val r = (argb ushr 16 and 0xFF) / 255f
    val g = (argb ushr 8 and 0xFF) / 255f
    val b = (argb and 0xFF) / 255f
    return Color(red = r, green = g, blue = b, alpha = a)
}

private fun colorToHex(color: Color): String {
    val argb = color.toArgb()
    return String.format("%08X", argb)
}

private fun hexToColor(hex: String): Color? {
    val clean = hex.removePrefix("#").trim()
    if (clean.length != 6 && clean.length != 8) return null
    return try {
        val argb = if (clean.length == 6) {
            (0xFF shl 24) or clean.toInt(16)
        } else {
            clean.toLong(16).toInt()
        }
        argbToComposeColor(argb)
    } catch (_: Exception) {
        null
    }
}
//?}