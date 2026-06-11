@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config.elements.base

import cc.polyfrost.oneconfig.config.core.OneColor
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
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.dsl.nanoVGHelper
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import tomeko.entitycrosshair.utils.*
import java.awt.Image
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.collections.iterator
import kotlin.math.ceil

abstract class BaseDrawer : BasicOption(null, null, "", "", "", "", 2) {
    // Shared UI state
    private var scroll = 0f
    private var scrollTarget = 0f
    private var scrollAnimation: Animation = DummyAnimation(0f)
    var inArea = false

    // Shared UI Controls
    protected val resetButton = BasicButton(64, 32, "Reset", 2, ColorPalette.PRIMARY_DESTRUCTIVE)
    protected val saveButton = BasicButton(64, 32, "Save", 2, ColorPalette.PRIMARY)
    protected val importButton = BasicButton(64, 32, "Import", 2, ColorPalette.SECONDARY)
    protected val exportButton = BasicButton(64, 32, "Export", 2, ColorPalette.SECONDARY)

    // --- Abstract Hooks implemented by Concrete Drawers ---
    abstract var currentCanvaSize: Int
    abstract val drawerMap: HashMap<Int, Int>
    abstract val currentCrosshair: BaseCrosshairEntry
    abstract fun createDefaultEntry(): BaseCrosshairEntry

    abstract fun drawPixel(index: Int, vg: Long, x: Float, y: Float, inputHandler: InputHandler)
    abstract fun setPixelToggled(index: Int, toggled: Boolean)
    abstract fun setPixelColor(index: Int, color: Int)
    abstract fun clear()

    abstract fun processRemoveQueue()
    abstract fun getCrosshairsCount(): Int
    abstract fun drawCrosshairElement(index: Int, vg: Long, x: Float, y: Float, inputHandler: InputHandler)
    abstract fun drawColorSelector(vg: Long, x: Float, y: Float, inputHandler: InputHandler)

    abstract fun saveAction(image: OneImage?)
    abstract fun updateTextureAction(image: OneImage)

    /**
     * Call this inside the concrete object init blocks to bind identical click behaviors.
     */
    protected fun initDrawer() {
        toBufferedImage(currentCrosshair.img)?.let { img ->
            if (img.width == 0 || img.height == 0) return@let
            loadImage(img, false, currentCrosshair)?.let { updateTextureAction(it) }
        }
        resetButton.setClickAction { runAsync { clear() } }
        saveButton.setClickAction { runAsync { saveAction(saveFromDrawer(false)) } }
        exportButton.setClickAction { runAsync { saveFromDrawer(false)?.let { copy(it.image) } } }
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
                } catch (_: Exception) {}
                if (image == null) image = IOUtils.getImageFromClipboard()
                if (image != null) {
                    loadImage(image!!.toBufferedImage(), true)
                } else {
                    notify("No image found in clipboard.")
                }
            }
        }
    }

    override fun draw(vg: Long, x: Int, y: Int, inputHandler: InputHandler) {
        val size = currentCanvaSize
        for (posY in 0..<size) {
            for (posX in 0..<size) {
                drawPixel(posToIndex(posX, posY), vg, x.toFloat(), y.toFloat(), inputHandler)
            }
        }

        if (size % 2 == 0) {
            nanoVGHelper.drawLine(vg, (x + 128).toFloat(), (y + 108).toFloat(), (x + 128).toFloat(), (y + 148).toFloat(), 1f, OneColor("703A3AFF").rgb)
            nanoVGHelper.drawLine(vg, (x + 108).toFloat(), (y + 128).toFloat(), (x + 148).toFloat(), (y + 128).toFloat(), 1f, OneColor("703A3AFF").rgb)
        }

        importButton.draw(vg, (x + 270).toFloat(), (y + 48).toFloat(), inputHandler)
        resetButton.draw(vg, (x + 270).toFloat(), (y + 174).toFloat(), inputHandler)
        saveButton.draw(vg, (x + 270).toFloat(), (y + 222).toFloat(), inputHandler)
        drawColorSelector(vg, (x + 270).toFloat(), (y + 126).toFloat(), inputHandler)
        exportButton.draw(vg, (x + 270).toFloat(), y.toFloat(), inputHandler)

        processRemoveQueue()

        val count = getCrosshairsCount()
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
            drawCrosshairElement(i, vg, x + 349 + posX * 165f, y + posY * 165f + scroll, inputHandler)
        }

        ScissorHelper.INSTANCE.resetScissor(vg, scissor)
    }

    fun Image.toBufferedImage(): BufferedImage {
        if (this is BufferedImage) return this
        val bufferedImage = BufferedImage(this.getWidth(null), this.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        val graphics2D = bufferedImage.createGraphics()
        graphics2D.drawImage(this, 0, 0, null)
        graphics2D.dispose()
        return bufferedImage
    }

    fun loadImage(image: BufferedImage?, save: Boolean, entry: BaseCrosshairEntry = createDefaultEntry()): OneImage? {
        val loadedImage = OneImage(image)
        val dimensionsSame = loadedImage.width == loadedImage.height
        val withinSize = loadedImage.width in 15..32
        if (!dimensionsSame || !withinSize) {
            val message = if (!dimensionsSame) "The width of the image must be equal to the height" else "The image must be between 15x15 and 32x32 pixels"
            notify("$message (width: ${loadedImage.width} height: ${loadedImage.height}).")
            return null
        }
        currentCrosshair.loadFrom(entry)
        currentCanvaSize = loadedImage.height
        val size = currentCanvaSize
        for (posY in 0..<size) {
            for (posX in 0..<size) {
                val c = loadedImage.image.getRGB(posX, posY)
                val idx = posToIndex(posX, posY)
                setPixelToggled(idx, c shr 24 != 0)
                setPixelColor(idx, c)
            }
        }
        if (save) saveAction(loadedImage)
        return loadedImage
    }

    fun saveFromDrawer(close: Boolean): OneImage? {
        val size = currentCanvaSize
        val image = OneImage(size, size)
        if (drawerMap.isEmpty() && !close) {
            notify("Crosshair can't be empty.")
            return null
        }
        for (i in drawerMap) {
            val pos = indexToPos(i.key)
            if (pos.x >= size || pos.y >= size) {
                setPixelToggled(i.key, false)
                continue
            }
            image.setColorAtPos(pos.x, pos.y, i.value)
        }
        return image
    }

    override fun finishUpAndClose() {
        val image = saveFromDrawer(true) ?: return
        currentCrosshair.img = toBase64(image.image)
        updateTextureAction(image)
    }

    override fun getHeight() = 256

    override fun keyTyped(key: Char, keyCode: Int) {
        if (mc.currentScreen !is OneConfigGui) return
        if (UKeyboard.isCtrlKeyDown() && keyCode == UKeyboard.KEY_S) {
            runAsync { saveAction(saveFromDrawer(false)) }
        }
    }
}