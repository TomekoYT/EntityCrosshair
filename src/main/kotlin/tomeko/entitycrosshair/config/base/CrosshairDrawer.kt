@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config.base

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.elements.BasicOption
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.gui.animations.Animation
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation
import cc.polyfrost.oneconfig.gui.animations.EaseOutQuad
import cc.polyfrost.oneconfig.gui.elements.BasicButton
import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.renderer.scissor.ScissorHelper
import cc.polyfrost.oneconfig.utils.IOUtils
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.Notifications
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.dsl.nanoVGHelper
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import tomeko.entitycrosshair.utils.Constants
import tomeko.entitycrosshair.utils.toBase64
import tomeko.entitycrosshair.utils.toBufferedImage
import tomeko.entitycrosshair.utils.copyToClipboard
import tomeko.entitycrosshair.utils.indexToPosition
import tomeko.entitycrosshair.utils.positionToIndex
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.collections.iterator
import kotlin.math.ceil

class CrosshairDrawer<T : CrosshairEntry>(
    private val canvaConfig: CanvaConfig<T>,
    private val entryFactory: () -> T,
    private val saveAction: (OneImage?) -> Unit,
    private val updateTextureAction: (OneImage) -> Unit,
) : BasicOption(null, null, "", "", "", "", 2) {
    private var scroll = 0f
    private var scrollTarget = 0f
    private var scrollAnimation: Animation = DummyAnimation(0f)
    var inArea = false

    private val resetButton = BasicButton(64, 32, "Reset", 2, ColorPalette.PRIMARY_DESTRUCTIVE)
    private val saveButton = BasicButton(64, 32, "Save", 2, ColorPalette.PRIMARY)
    private val importButton = BasicButton(64, 32, "Import", 2, ColorPalette.SECONDARY)
    private val exportButton = BasicButton(64, 32, "Export", 2, ColorPalette.SECONDARY)

    val pixels: Array<CrosshairPixel<T>> =
        Array(Constants.MAX_CANVAS_SIZE * Constants.MAX_CANVAS_SIZE) { CrosshairPixel(it, canvaConfig) }
    val elements = HashMap<T, CrosshairPresetElement<T>>()
    val removeQueue = ArrayList<T>()
    private val colorSelector = CrosshairColorSelector(canvaConfig)

    private val currentCrosshair get() = canvaConfig.newCurrentCrosshair

    private var lastSyncedSnapshot: Map<Int, Int>? = null

    private fun applyToCurrent(image: OneImage) {
        currentCrosshair.img = toBase64(image.image)
        updateTextureAction(image)
        lastSyncedSnapshot = HashMap(canvaConfig.drawerMap)
    }

    private fun syncIfChanged() {
        if (canvaConfig.drawerMap == lastSyncedSnapshot) return
        saveFromDrawer(true)?.let { applyToCurrent(it) }
    }

    init {
        toBufferedImage(currentCrosshair.img)?.let { img ->
            if (img.width != 0 && img.height != 0) {
                loadImage(img, false, currentCrosshair)
            }
        }
        resetButton.setClickAction { runAsync { clear() } }
        saveButton.setClickAction {
            runAsync {
                val image = saveFromDrawer(false) ?: return@runAsync
                saveAction(image)
                applyToCurrent(image)
            }
        }
        exportButton.setClickAction { runAsync { saveFromDrawer(false)?.let { copyToClipboard(it.image) } } }
        importButton.setClickAction {
            runAsync {
                var image: Image? = null
                try {
                    val hopefullyAList = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
                        .getTransferData(DataFlavor.javaFileListFlavor)
                    if (hopefullyAList is List<*>) {
                        if (hopefullyAList.isEmpty() || hopefullyAList[0] !is File) return@runAsync
                        val file = hopefullyAList[0] as File
                        ImageIO.read(file)?.let { image = it }
                    }
                } catch (_: Exception) {
                }
                if (image == null) image = IOUtils.getImageFromClipboard()
                if (image != null) {
                    loadImage(image!!.toBufferedImage(), true)
                } else {
                    Notifications.INSTANCE.send(Constants.MOD_NAME, "No image found in clipboard.")
                }
            }
        }
    }

    fun clear() {
        for (pixel in pixels) pixel.isToggled = false
    }

    fun saveCurrent() = saveAction(saveFromDrawer(false))

    fun getElement(entry: T): CrosshairPresetElement<T> =
        elements.getOrPut(entry) { CrosshairPresetElement(entry, this) }

    private fun processRemoveQueue() {
        for (entry in removeQueue) {
            canvaConfig.newCrosshairs.remove(entry)
            getElement(entry).onRemove()
            elements.remove(entry)
        }
        removeQueue.clear()
    }

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        val size = canvaConfig.canvaSize
        for (posY in 0..<size) {
            for (posX in 0..<size) {
                pixels[positionToIndex(posX, posY)].draw(vg, x.toFloat(), y.toFloat(), inputHandler)
            }
        }

        syncIfChanged()

        if (size % 2 == 0) {
            nanoVGHelper.drawLine(
                vg,
                (x + 128).toFloat(),
                (y + 108).toFloat(),
                (x + 128).toFloat(),
                (y + 148).toFloat(),
                1f,
                OneColor("703A3AFF").rgb
            )
            nanoVGHelper.drawLine(
                vg,
                (x + 108).toFloat(),
                (y + 128).toFloat(),
                (x + 148).toFloat(),
                (y + 128).toFloat(),
                1f,
                OneColor("703A3AFF").rgb
            )
        }

        importButton.draw(vg, (x + 270).toFloat(), (y + 48).toFloat(), inputHandler)
        resetButton.draw(vg, (x + 270).toFloat(), (y + 174).toFloat(), inputHandler)
        saveButton.draw(vg, (x + 270).toFloat(), (y + 222).toFloat(), inputHandler)
        colorSelector.draw(vg, (x + 270).toFloat(), (y + 126).toFloat(), inputHandler)
        exportButton.draw(vg, (x + 270).toFloat(), y.toFloat(), inputHandler)

        processRemoveQueue()

        val count = canvaConfig.newCrosshairs.size
        val height = (149 + 16) * ceil(count / 4f) - 16

        if (height <= 256) scrollAnimation = DummyAnimation(0f)
        scroll = scrollAnimation.get()

        val scissor = ScissorHelper.INSTANCE.scissor(vg, (x + 349).toFloat(), y.toFloat(), 644f, 256f)
        inArea = scissor.isInScissor(inputHandler.mouseX(), inputHandler.mouseY())

        if (inArea) {
            inputHandler.unblockDWheel()
            val dWheel = inputHandler.dWheel.toFloat()
            inputHandler.blockDWheel()

            if (dWheel != 0f) {
                scrollTarget += dWheel
                if (scrollTarget > 0f) scrollTarget = 0f
                else if (scrollTarget < 256 - height) scrollTarget = (256 - height)
                scrollAnimation = EaseOutQuad(150, scroll, scrollTarget, false)
            }
        } else {
            inputHandler.unblockDWheel()
            if (mc.currentScreen is OneConfigGui) inputHandler.stopBlockingInput()
        }

        for (i in 0..<count) {
            val posX = i % 4
            val posY = i / 4
            getElement(canvaConfig.newCrosshairs[i]).draw(
                vg,
                x + 349 + posX * 165f,
                y + posY * 165f + scroll,
                inputHandler
            )
        }

        ScissorHelper.INSTANCE.resetScissor(vg, scissor)
    }

    fun loadImage(image: BufferedImage?, save: Boolean, entry: T = entryFactory()): OneImage? {
        val loadedImage = OneImage(image)
        val dimensionsSame = loadedImage.width == loadedImage.height
        val withinSize = loadedImage.width in Constants.MIN_CANVAS_SIZE..Constants.MAX_CANVAS_SIZE
        if (!dimensionsSame || !withinSize) {
            val message =
                if (!dimensionsSame) "The width of the image must be equal to the height" else "The image must be between ${Constants.MIN_CANVAS_SIZE}x${Constants.MIN_CANVAS_SIZE} and ${Constants.MAX_CANVAS_SIZE}x${Constants.MAX_CANVAS_SIZE} pixels"
            Notifications.INSTANCE.send(
                Constants.MOD_NAME,
                "$message (width: ${loadedImage.width} height: ${loadedImage.height})."
            )
            return null
        }
        currentCrosshair.loadFrom(entry)
        canvaConfig.canvaSize = loadedImage.height
        val size = canvaConfig.canvaSize
        for (posY in 0..<size) {
            for (posX in 0..<size) {
                val c = loadedImage.image.getRGB(posX, posY)
                val idx = positionToIndex(posX, posY)
                pixels[idx].isToggled = c shr 24 != 0
                pixels[idx].color = c
            }
        }
        if (save) saveAction(loadedImage)
        applyToCurrent(loadedImage)
        return loadedImage
    }

    fun saveFromDrawer(close: Boolean): OneImage? {
        val size = canvaConfig.canvaSize
        val image = OneImage(size, size)
        if (canvaConfig.drawerMap.isEmpty() && !close) {
            Notifications.INSTANCE.send(Constants.MOD_NAME, "Crosshair can't be empty.")
            return null
        }
        for ((key, value) in canvaConfig.drawerMap) {
            val pos = indexToPosition(key)
            if (pos.x >= size || pos.y >= size) {
                pixels[key].isToggled = false
                continue
            }
            image.setColorAtPos(pos.x, pos.y, value)
        }
        return image
    }

    fun flush() {
        val image = saveFromDrawer(true) ?: return
        applyToCurrent(image)
    }

    override fun finishUpAndClose() = flush()

    override fun getHeight() = 256

    override fun keyTyped(key: Char, keyCode: Int) {
        if (mc.currentScreen !is OneConfigGui) return
        if (UKeyboard.isCtrlKeyDown() && keyCode == UKeyboard.KEY_S) {
            runAsync {
                val image = saveFromDrawer(false) ?: return@runAsync
                saveAction(image)
                applyToCurrent(image)
            }
        }
    }
}
*///?}