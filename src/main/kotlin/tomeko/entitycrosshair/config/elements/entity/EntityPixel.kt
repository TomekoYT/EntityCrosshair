package tomeko.entitycrosshair.config.elements.entity

import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.elements.base.BasePixel

class EntityPixel(index: Int) : BasePixel(index) {
    override val canvaSize: Int
        get() = EntityCrosshairConfig.entityCanvaConfig.canvaSize

    override val penColorRgb: Int
        get() = EntityCrosshairConfig.entityCanvaConfig.penColor.rgb

    override val drawerMap: HashMap<Int, Int>
        get() = EntityCrosshairConfig.entityCanvaConfig.drawerMap
}