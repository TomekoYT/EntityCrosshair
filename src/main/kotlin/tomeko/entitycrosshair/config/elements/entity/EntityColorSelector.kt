package tomeko.entitycrosshair.config.elements.entity

import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.elements.base.BaseColorSelector

class EntityColorSelector : BaseColorSelector() {
    override var targetPenColor: OneColor
        get() = EntityCrosshairConfig.entityCanvaConfig.penColor
        set(value) { EntityCrosshairConfig.entityCanvaConfig.penColor = value }
}