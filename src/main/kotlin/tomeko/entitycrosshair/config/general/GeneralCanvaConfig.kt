package tomeko.entitycrosshair.config.general

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.annotations.CustomOption
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.base.CanvaConfig

class GeneralCanvaConfig : CanvaConfig<GeneralCrosshairEntry> {
    @Exclude
    override var drawerMap = HashMap<Int, Int>()

    @CustomOption(id = EntityCrosshairConfig.CATEGORY_GENERAL)
    override var newCrosshairs = arrayListOf(GeneralCrosshairEntry())

    override var penColor = OneColor(-1)

    @Slider(
        name = "Canva Size",
        min = 15f, max = 32f,
        category = EntityCrosshairConfig.CATEGORY_GENERAL
    )
    override var canvaSize = 15
        get() = field.coerceIn(15, 32)

    override var newCurrentCrosshair = GeneralCrosshairEntry()
}
*///?}