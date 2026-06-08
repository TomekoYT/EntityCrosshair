@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config.elements

import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.gui.OneConfigGui
import cc.polyfrost.oneconfig.gui.elements.BasicElement
import cc.polyfrost.oneconfig.utils.InputHandler
import cc.polyfrost.oneconfig.utils.color.ColorPalette
import cc.polyfrost.oneconfig.utils.dsl.nanoVGHelper
import tomeko.entitycrosshair.utils.indexToPos

class Pixel(val canvaConfig: CanvaConfig, val index: Int) : BasicElement(16, 16, ColorPalette.PRIMARY, true, 0f) {
    var backgroundColor = 0
    var posX = 0
    var posY = 0

    var color = -1
        set(value) {
            if (value shr 24 == 0) isToggled = false
            canvaConfig.drawerMap[index] = value
            field = value
        }

    var lastToggled = false

    override fun draw(vg: Long, x: Float, y: Float, inputHandler: InputHandler) {
        val size = (256 - (canvaConfig.canvaSize - 1)) / canvaConfig.canvaSize.toFloat()
        posX = index % 32
        posY = index / 32
        val x1 = x + posX * (size + 1)
        val y1 = y + posY * (size + 1)
        width = size.toInt()
        height = size.toInt()
        this.update(x1, y1, inputHandler)
        nanoVGHelper.drawRect(vg, x1, y1, size, size, currentColor)
    }

    override fun update(x: Float, y: Float, inputHandler: InputHandler) {
        val pos = indexToPos(index)
        val size = canvaConfig.canvaSize
        backgroundColor = if (size % 2 == 1 && pos.x == size / 2 && pos.x == pos.y) {
            OneColor("703A3AFF").rgb
        } else if ((pos.x + pos.y) % 2 == 0) {
            ColorPalette.SECONDARY.normalColor
        } else {
            ColorPalette.SECONDARY.hoveredColor
        }
        hovered = inputHandler.isAreaHovered(x - hitBoxX, y - hitBoxY, (width + hitBoxX).toFloat(), (height + hitBoxY).toFloat())
        if (hovered && OneConfigGui.INSTANCE.currentColorSelector == null) {
            if (inputHandler.isMouseDown) {
                set(true, canvaConfig.penColor.rgb)
            }
            if (inputHandler.isMouseDown(1)) {
                set(false, color)
            }
        }
        if (lastToggled != isToggled) {
            lastToggled = isToggled
            if (isToggled) {
                canvaConfig.drawerMap[index] = color
            } else {
                canvaConfig.drawerMap.remove(index)
            }
        }
        currentColor = if (isToggled) color else backgroundColor
    }

    fun set(toggle: Boolean, color: Int) {
        isToggled = toggle
        if (!toggle) return
        this.color = color
    }
}