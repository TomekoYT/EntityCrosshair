package tomeko.entitycrosshair.config.elements.general

import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.elements.base.BasePixel

class GeneralPixel(index: Int) : BasePixel(index) {
    override val canvaSize: Int
        get() = EntityCrosshairConfig.generalCanvaConfig.canvaSize

    override val penColorRgb: Int
        get() = EntityCrosshairConfig.generalCanvaConfig.penColor.rgb

    override val drawerMap: HashMap<Int, Int>
        get() = EntityCrosshairConfig.generalCanvaConfig.drawerMap
}