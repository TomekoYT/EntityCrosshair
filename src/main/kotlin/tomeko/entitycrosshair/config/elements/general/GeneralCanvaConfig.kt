package tomeko.entitycrosshair.config.elements.general

import cc.polyfrost.oneconfig.config.annotations.CustomOption
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig

class GeneralCanvaConfig {
    @Exclude
    var drawerMap = HashMap<Int, Int>()

    @CustomOption(id = EntityCrosshairConfig.CATEGORY_GENERAL)
    var newCrosshairs = arrayListOf(GeneralCrosshairEntry())

    var penColor = OneColor(-1)

    @Slider(
        name = "Canva Size",
        min = 15f, max = 32f,
        category = EntityCrosshairConfig.CATEGORY_GENERAL
    )
    var canvaSize = 15
        get() = field.coerceIn(15, 32)

    var newCurrentCrosshair = GeneralCrosshairEntry()
}