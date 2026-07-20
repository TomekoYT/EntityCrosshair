package tomeko.entitycrosshair.config.entity

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.annotations.CustomOption
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.base.CanvaConfig
import tomeko.entitycrosshair.utils.Constants

class EntityCanvaConfig : CanvaConfig<EntityCrosshairEntry> {
    @Exclude
    override var drawerMap = HashMap<Int, Int>()

    @CustomOption(id = EntityCrosshairConfig.CATEGORY_ENTITY)
    override var newCrosshairs = arrayListOf(EntityCrosshairEntry())

    override var penColor = OneColor(-1)

    @Slider(
        name = "Canva Size",
        min = Constants.MIN_CANVA_SIZE.toFloat(), max = Constants.MAX_CANVA_SIZE.toFloat(),
        category = EntityCrosshairConfig.CATEGORY_ENTITY
    )
    override var canvaSize = Constants.MIN_CANVA_SIZE
        get() = field.coerceIn(Constants.MIN_CANVA_SIZE, Constants.MAX_CANVA_SIZE)

    override var newCurrentCrosshair = EntityCrosshairEntry()
}
*///?}