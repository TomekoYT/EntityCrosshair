package tomeko.entitycrosshair.config.general

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.annotations.CustomOption
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.base.CanvaConfig
import tomeko.entitycrosshair.utils.Constants

class GeneralCanvaConfig : CanvaConfig<GeneralCrosshairEntry> {
    @Exclude
    override var drawerMap = HashMap<Int, Int>()

    @CustomOption(id = EntityCrosshairConfig.CATEGORY_GENERAL)
    override var newCrosshairs = arrayListOf(GeneralCrosshairEntry())

    override var penColor = OneColor(-1)

    @Slider(
        name = "Canva Size",
        min = Constants.MIN_CANVAS_SIZE.toFloat(), max = Constants.MAX_CANVAS_SIZE.toFloat(),
        category = EntityCrosshairConfig.CATEGORY_GENERAL
    )
    override var canvaSize = Constants.MIN_CANVAS_SIZE
        get() = field.coerceIn(Constants.MIN_CANVAS_SIZE, Constants.MAX_CANVAS_SIZE)

    override var newCurrentCrosshair = GeneralCrosshairEntry()
}
*///?}