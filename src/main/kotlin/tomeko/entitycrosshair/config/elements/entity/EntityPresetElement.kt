package tomeko.entitycrosshair.config.elements.entity

import tomeko.entitycrosshair.config.elements.base.BasePresetElement
import java.awt.image.BufferedImage

class EntityPresetElement(val entityCrosshair: EntityCrosshairEntry) : BasePresetElement(entityCrosshair) {
    override val drawerInArea: Boolean
        get() = EntityDrawer.inArea

    override fun addToRemoveQueue() {
        EntityDrawer.removeQueue.add(entityCrosshair)
    }

    override fun removeFromDrawerElements() {
        EntityDrawer.elements.remove(entityCrosshair)
    }

    override fun clearDrawer() {
        EntityDrawer.clear()
    }

    override fun loadDrawerImage(img: BufferedImage?) {
        EntityDrawer.loadImage(img, false, entityCrosshair)
    }
}