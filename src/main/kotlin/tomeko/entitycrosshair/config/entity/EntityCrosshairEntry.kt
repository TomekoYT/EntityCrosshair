package tomeko.entitycrosshair.config.entity

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.base.CrosshairEntry
import tomeko.entitycrosshair.config.base.EntityDrawer

class EntityCrosshairEntry(
    override var img: String = "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAALklEQVR42mNgGH7gPxCQrAGmCZkmyiBkRbjYtPEjPmfT3nay/UxRaFMUz0MDAAARS2Od/Bu+BgAAAABJRU5ErkJggg==",
) : CrosshairEntry {
    @Slider(name = "Scale %", min = 0f, max = 1000f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    override var scale = 100

    @Slider(name = "Rotation", min = -180f, max = 180f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    override var rotation = 0

    @Slider(name = "X Offset", min = -1920f, max = 1920f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    override var offsetX = 0

    @Slider(name = "Y Offset", min = -1080f, max = 1080f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    override var offsetY = 0

    @Switch(
        name = "Centered",
        description = "In vanilla Minecraft, the crosshair is not centered. Enable this option to center the crosshair.",
        category = EntityCrosshairConfig.CATEGORY_ENTITY
    )
    override var centered = true

    @Button(name = "Transform", text = "Reset", size = 1, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    var transformReset = Runnable {
        runAsync {
            val canvaConfig = EntityCrosshairConfig.entityCanvaConfig
            val img = canvaConfig.newCurrentCrosshair.img
            canvaConfig.newCurrentCrosshair.loadFrom(EntityCrosshairEntry())
            canvaConfig.newCurrentCrosshair.img = img
            EntityDrawer.saveCurrent()
        }
    }
}
*///?}