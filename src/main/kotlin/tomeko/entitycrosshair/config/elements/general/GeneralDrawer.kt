package tomeko.entitycrosshair.config.elements.general

import cc.polyfrost.oneconfig.images.OneImage
import cc.polyfrost.oneconfig.utils.InputHandler
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.elements.base.BaseCrosshairEntry
import tomeko.entitycrosshair.config.elements.base.BaseDrawer
import tomeko.entitycrosshair.render.CrosshairRenderer
import tomeko.entitycrosshair.utils.saveDefault

object GeneralDrawer : BaseDrawer() {
    val pixels: Array<GeneralPixel> = Array(1024) { GeneralPixel(it) }
    var elements = HashMap<GeneralCrosshairEntry, GeneralPresetElement>()
    var removeQueue = ArrayList<GeneralCrosshairEntry>()
    private val colorSelector = GeneralColorSelector()

    init {
        initDrawer()
    }

    override var currentCanvaSize: Int
        get() = EntityCrosshairConfig.generalCanvaConfig.canvaSize
        set(value) { EntityCrosshairConfig.generalCanvaConfig.canvaSize = value }

    override val drawerMap: HashMap<Int, Int>
        get() = EntityCrosshairConfig.generalCanvaConfig.drawerMap

    override val currentCrosshair: BaseCrosshairEntry
        get() = EntityCrosshairConfig.generalCanvaConfig.newCurrentCrosshair

    override fun createDefaultEntry(): BaseCrosshairEntry = GeneralCrosshairEntry()

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
            EntityCrosshairConfig.generalCanvaConfig.newCrosshairs.remove(i)
            getElement(i).onRemove()
            elements.remove(i)
        }
        removeQueue.clear()
    }

    override fun getCrosshairsCount(): Int = EntityCrosshairConfig.generalCanvaConfig.newCrosshairs.size

    override fun drawCrosshairElement(index: Int, vg: Long, x: Float, y: Float, inputHandler: InputHandler) {
        val entry = EntityCrosshairConfig.generalCanvaConfig.newCrosshairs[index]
        getElement(entry).draw(vg, x, y, inputHandler)
    }

    override fun drawColorSelector(vg: Long, x: Float, y: Float, inputHandler: InputHandler) {
        colorSelector.draw(vg, x, y, inputHandler)
    }

    override fun saveAction(image: OneImage?) = saveDefault(image)

    override fun updateTextureAction(image: OneImage) = CrosshairRenderer.updateDefaultTexture(image)

    fun getElement(entry: GeneralCrosshairEntry): GeneralPresetElement {
        elements[entry] ?: elements.put(entry, GeneralPresetElement(entry))
        return elements[entry]!!
    }
}