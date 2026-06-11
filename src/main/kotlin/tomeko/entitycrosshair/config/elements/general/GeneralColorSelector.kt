package tomeko.entitycrosshair.config.elements.general

import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.elements.base.BaseColorSelector

class GeneralColorSelector : BaseColorSelector() {
    override var targetPenColor: OneColor
        get() = EntityCrosshairConfig.generalCanvaConfig.penColor
        set(value) { EntityCrosshairConfig.generalCanvaConfig.penColor = value }
}