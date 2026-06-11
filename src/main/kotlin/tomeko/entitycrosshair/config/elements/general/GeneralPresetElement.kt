package tomeko.entitycrosshair.config.elements.general

import tomeko.entitycrosshair.config.elements.base.BasePresetElement
import java.awt.image.BufferedImage

class GeneralPresetElement(val defaultCrosshair: GeneralCrosshairEntry) : BasePresetElement(defaultCrosshair) {
    override val drawerInArea: Boolean
        get() = GeneralDrawer.inArea

    override fun addToRemoveQueue() {
        GeneralDrawer.removeQueue.add(defaultCrosshair)
    }

    override fun removeFromDrawerElements() {
        GeneralDrawer.elements.remove(defaultCrosshair)
    }

    override fun clearDrawer() {
        GeneralDrawer.clear()
    }

    override fun loadDrawerImage(img: BufferedImage?) {
        GeneralDrawer.loadImage(img, false, defaultCrosshair)
    }
}