package tomeko.entitycrosshair.config.entity

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.annotations.CustomOption
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.core.OneColor
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.base.CanvaConfig

class EntityCanvaConfig : CanvaConfig<EntityCrosshairEntry> {
    @Exclude
    override var drawerMap = HashMap<Int, Int>()

    @CustomOption(id = EntityCrosshairConfig.CATEGORY_ENTITY)
    override var newCrosshairs = arrayListOf(EntityCrosshairEntry())

    override var penColor = OneColor(-1)

    @Slider(
        name = "Canva Size",
        min = 15f, max = 32f,
        category = EntityCrosshairConfig.CATEGORY_ENTITY
    )
    override var canvaSize = 15
        get() = field.coerceIn(15, 32)

    override var newCurrentCrosshair = EntityCrosshairEntry()
}
*///?}