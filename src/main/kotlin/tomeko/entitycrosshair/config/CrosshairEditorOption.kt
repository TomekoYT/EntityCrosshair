package tomeko.entitycrosshair.config

//? if >= 26.1 {
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.polyfrost.oneconfig.api.config.v1.Property
import org.polyfrost.oneconfig.api.config.v1.Visualizer
import org.polyfrost.oneconfig.api.config.v1.annotations.Option
import tomeko.entitycrosshair.config.CrosshairRenderer.toPngBytes
import tomeko.entitycrosshair.utils.Constants
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
        var penColor by remember(prop.id) {
            mutableStateOf(
                Color(
                    if (entityMode) EntityCrosshairConfig.entityColor.argb
                    else EntityCrosshairConfig.generalColor.argb
                )
            )
        }
        var canvasSize by remember(prop.id) { mutableStateOf(setData.canvasSize.coerceIn(Constants.MIN_CANVAS_SIZE, Constants.MAX_CANVAS_SIZE)) }
        var canvasSizeText by remember { mutableStateOf(canvasSize.toString()) }

        fun renderPixelsToImage(size: Int): BufferedImage {
            val img = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
            for (y in 0 until size) {
                for (x in 0 until size) {
                    img.setRGB(x, y, pixels.value[x + y * Constants.MAX_CANVAS_SIZE] ?: 0)
                }
            }
            return img
        }

        LaunchedEffect(prop.id, pixels.value, canvasSize) {
            val pngBytes = renderPixelsToImage(canvasSize).toPngBytes()
            if (entityMode) CrosshairRenderer.updateEntityTexture(pngBytes) else CrosshairRenderer.updateDefaultTexture(
                pngBytes
            )
        }

        LaunchedEffect(
            EntityCrosshairConfig.generalColor.argb,
            EntityCrosshairConfig.entityColor.argb
        ) {
            penColor = Color(
                if (entityMode)
                    EntityCrosshairConfig.entityColor.argb
                else
                    EntityCrosshairConfig.generalColor.argb
            )
        }

        fun persist(newSetData: CrosshairSetData) {
            setData = newSetData

            @Suppress("UNCHECKED_CAST")
            (prop as Property<Any>).set(newSetData.encode())

            if (entityMode) EntityCrosshairConfig.onEntityChanged(newSetData) else EntityCrosshairConfig.onGeneralChanged(
                newSetData
            )
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

        DisposableEffect(Unit) {
            onDispose {
                saveCurrent()
            }
        }

        fun loadImageIntoEditor(image: BufferedImage) {
            if (image.width != image.height || image.width !in Constants.MIN_CANVAS_SIZE..Constants.MAX_CANVAS_SIZE) {
                return
            }
            canvasSize = image.height
            val newPixels = HashMap<Int, Int>()
            for (y in 0 until image.height) {
                for (x in 0 until image.width) {
                    val c = image.getRGB(x, y)
                    if (c ushr 24 != 0) newPixels[x + y * Constants.MAX_CANVAS_SIZE] = c
                }
            }
            pixels.value = newPixels
        }

        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    "Canvas size:",
                    color = Color.White,
                    modifier = Modifier.width(100.dp)
                )

                Slider(
                    value = canvasSize.toFloat(),
                    onValueChange = {
                        canvasSize = it.toInt().coerceIn(
                            Constants.MIN_CANVAS_SIZE,
                            Constants.MAX_CANVAS_SIZE
                        )
                        canvasSizeText = canvasSize.toString()
                    },
                    valueRange = Constants.MIN_CANVAS_SIZE.toFloat()..
                            Constants.MAX_CANVAS_SIZE.toFloat(),
                    steps = Constants.MAX_CANVAS_SIZE - Constants.MIN_CANVAS_SIZE - 1,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.width(8.dp))

                OutlinedTextField(
                    value = canvasSizeText,
                    onValueChange = { text ->
                        if (text.all(Char::isDigit)) {
                            canvasSizeText = text

                            text.toIntOrNull()?.let { value ->
                                canvasSize = value.coerceIn(
                                    Constants.MIN_CANVAS_SIZE,
                                    Constants.MAX_CANVAS_SIZE
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.width(80.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        disabledTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                    )
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
                                val idx = gx + gy * Constants.MAX_CANVAS_SIZE
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

            LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.height(340.dp)) {
                items(setData.presets) { preset ->
                    Column(
                        modifier = Modifier.padding(8.dp).pointerInput(preset) {
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
                                modifier = Modifier
                                    .size(200.dp)
                                    .background(Color(0xFF2E323C)),
                                filterQuality = FilterQuality.None,
                            )
                        }
                        Button(
                            onClick = {
                                persist(setData.copy(presets = setData.presets.filterNot { it === preset }
                                    .toMutableList()))
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD32F2F)),
                            contentPadding = PaddingValues(6.dp),
                            modifier = Modifier.padding(top = 6.dp),
                        ) {
                            TrashIcon(modifier = Modifier.size(16.dp), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrashIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val strokeW = (w * 0.1f).coerceAtLeast(1f)
        val lidY = h * 0.24f
        val bodyTop = h * 0.34f
        val bodyBottom = h * 0.86f
        val bodyLeft = h * 0.24f
        val bodyRight = w - bodyLeft


        drawLine(color, Offset(w * 0.12f, lidY), Offset(w * 0.88f, lidY), strokeWidth = strokeW)

        drawLine(color, Offset(w * 0.36f, lidY), Offset(w * 0.40f, h * 0.08f), strokeWidth = strokeW)
        drawLine(color, Offset(w * 0.64f, lidY), Offset(w * 0.60f, h * 0.08f), strokeWidth = strokeW)
        drawLine(color, Offset(w * 0.40f, h * 0.08f), Offset(w * 0.60f, h * 0.08f), strokeWidth = strokeW)

        drawLine(color, Offset(bodyLeft, bodyTop), Offset(w * 0.30f, bodyBottom), strokeWidth = strokeW)
        drawLine(color, Offset(bodyRight, bodyTop), Offset(w * 0.70f, bodyBottom), strokeWidth = strokeW)
        drawLine(color, Offset(w * 0.30f, bodyBottom), Offset(w * 0.70f, bodyBottom), strokeWidth = strokeW)

        drawLine(
            color,
            Offset(w * 0.5f, bodyTop + strokeW),
            Offset(w * 0.5f, bodyBottom - strokeW),
            strokeWidth = strokeW * 0.8f
        )
    }
}

private val CENTER_MARK_COLOR = Color(0xFF9C3232)

private fun DrawScope.drawGrid(canvasSize: Int, cellPx: Float, pixels: Map<Int, Int>) {
    val isOdd = canvasSize % 2 == 1
    val centerIndex = canvasSize / 2

    for (y in 0 until canvasSize) {
        for (x in 0 until canvasSize) {
            val idx = x + y * Constants.MAX_CANVAS_SIZE
            val argb = pixels[idx]
            val checker = if ((x + y) % 2 == 0) Color(0xFF3A3F4B) else Color(0xFF2E323C)
            val isCenterCell = isOdd && x == centerIndex && y == centerIndex
            val background = if (isCenterCell) CENTER_MARK_COLOR else checker
            val color = if (argb != null) argbToComposeColor(argb) else background
            drawRect(
                color = color,
                topLeft = Offset(x * cellPx, y * cellPx),
                size = androidx.compose.ui.geometry.Size(cellPx - 1f, cellPx - 1f),
            )
        }
    }

    if (!isOdd) {
        val centerPx = centerIndex * cellPx
        val armLength = (cellPx * 0.75f).coerceAtLeast(4f)
        drawLine(
            color = Color.Red,
            start = Offset(centerPx, centerPx - armLength),
            end = Offset(centerPx, centerPx + armLength),
            strokeWidth = 2f,
        )
        drawLine(
            color = Color.Red,
            start = Offset(centerPx - armLength, centerPx),
            end = Offset(centerPx + armLength, centerPx),
            strokeWidth = 2f,
        )
    }
}

private fun loadPixelsFromBase64(base64: String): Map<Int, Int> {
    val image = toBufferedImage(base64) ?: return emptyMap()
    val map = HashMap<Int, Int>()
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val c = image.getRGB(x, y)
            if (c ushr 24 != 0) map[x + y * Constants.MAX_CANVAS_SIZE] = c
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