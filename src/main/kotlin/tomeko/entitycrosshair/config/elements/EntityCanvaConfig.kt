package tomeko.entitycrosshair.config.elements

import cc.polyfrost.oneconfig.config.annotations.CustomOption
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig

class EntityCanvaConfig {
    @Exclude
    var drawerMap = HashMap<Int, Int>()

    @CustomOption(id = EntityCrosshairConfig.CATEGORY_ENTITY)
    var newCrosshairs = arrayListOf(EntityCrosshairEntry())

    var penColor = OneColor(-1)

    @Slider(
        name = "Canva Size",
        min = 15f, max = 32f,
        category = EntityCrosshairConfig.CATEGORY_ENTITY
    )
    var canvaSize = 15
        get() = field.coerceIn(15, 32)

    var newCurrentCrosshair = EntityCrosshairEntry()
}