@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config.elements.entity

import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.utils.InputHandler
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.elements.base.BaseCrosshairEntry
import tomeko.entitycrosshair.config.elements.base.BaseDrawer
import tomeko.entitycrosshair.render.CrosshairRenderer
import tomeko.entitycrosshair.utils.saveEntity

object EntityDrawer : BaseDrawer() {
    val pixels: Array<EntityPixel> = Array(1024) { EntityPixel(it) }
    var elements = HashMap<EntityCrosshairEntry, EntityPresetElement>()
    var removeQueue = ArrayList<EntityCrosshairEntry>()
    private val colorSelector = EntityColorSelector()

    init {
        initDrawer()
    }

    override var currentCanvaSize: Int
        get() = EntityCrosshairConfig.entityCanvaConfig.canvaSize
        set(value) { EntityCrosshairConfig.entityCanvaConfig.canvaSize = value }

    override val drawerMap: HashMap<Int, Int>
        get() = EntityCrosshairConfig.entityCanvaConfig.drawerMap

    override val currentCrosshair: BaseCrosshairEntry
        get() = EntityCrosshairConfig.entityCanvaConfig.newCurrentCrosshair

    override fun createDefaultEntry(): BaseCrosshairEntry = EntityCrosshairEntry()

    override fun drawPixel(index: Int, vg: Long, x: Float, y: Float, inputHandler: InputHandler) {
        pixels[index].draw(vg, x, y, inputHandler)
    }

    override fun setPixelToggled(index: Int, toggled: Boolean) {
        pixels[index].isToggled = toggled
    }

    override fun setPixelColor(index: Int, color: Int) {
        pixels[index].color = color
    }

    override fun clear() {
        for (i in pixels) i.isToggled = false
    }

    override fun processRemoveQueue() {
        for (i in removeQueue) {
            EntityCrosshairConfig.entityCanvaConfig.newCrosshairs.remove(i)
            getElement(i).onRemove()
            elements.remove(i)
        }
        removeQueue.clear()
    }

    override fun getCrosshairsCount(): Int = EntityCrosshairConfig.entityCanvaConfig.newCrosshairs.size

    override fun drawCrosshairElement(index: Int, vg: Long, x: Float, y: Float, inputHandler: InputHandler) {
        val entry = EntityCrosshairConfig.entityCanvaConfig.newCrosshairs[index]
        getElement(entry).draw(vg, x, y, inputHandler)
    }

    override fun drawColorSelector(vg: Long, x: Float, y: Float, inputHandler: InputHandler) {
        colorSelector.draw(vg, x, y, inputHandler)
    }

    override fun saveAction(image: OneImage?) = saveEntity(image)

    override fun updateTextureAction(image: OneImage) = CrosshairRenderer.updateEntityTexture(image)

    fun getElement(entry: EntityCrosshairEntry): EntityPresetElement {
        elements[entry] ?: elements.put(entry, EntityPresetElement(entry))
        return elements[entry]!!
    }
}