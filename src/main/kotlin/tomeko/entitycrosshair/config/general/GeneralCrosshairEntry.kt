package tomeko.entitycrosshair.config.general

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.base.CrosshairEntry
import tomeko.entitycrosshair.config.base.GeneralDrawer

class GeneralCrosshairEntry(
    override var img: String = "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAAHklEQVR42mNgGH7gPxAMQc0k2wQDyDaji4+G9pADAJoJM827QMwQAAAAAElFTkSuQmCC",
) : CrosshairEntry {
    @Slider(name = "Scale %", min = 0f, max = 1000f, category = EntityCrosshairConfig.CATEGORY_GENERAL)
    override var scale = 100

    @Slider(name = "Rotation", min = -180f, max = 180f, category = EntityCrosshairConfig.CATEGORY_GENERAL)
    override var rotation = 0

    @Slider(name = "X Offset", min = -1920f, max = 1920f, category = EntityCrosshairConfig.CATEGORY_GENERAL)
    override var offsetX = 0

    @Slider(name = "Y Offset", min = -1080f, max = 1080f, category = EntityCrosshairConfig.CATEGORY_GENERAL)
    override var offsetY = 0

    @Switch(
        name = "Centered",
        description = "In vanilla Minecraft, the crosshair is not centered. Enable this option to center the crosshair.",
        category = EntityCrosshairConfig.CATEGORY_GENERAL
    )
    override var centered = true

    @Button(name = "Transform", text = "Reset", size = 1, category = EntityCrosshairConfig.CATEGORY_GENERAL)
    var transformReset = Runnable {
        runAsync {
            val canvaConfig = EntityCrosshairConfig.generalCanvaConfig
            val img = canvaConfig.newCurrentCrosshair.img
            canvaConfig.newCurrentCrosshair.loadFrom(GeneralCrosshairEntry())
            canvaConfig.newCurrentCrosshair.img = img
            GeneralDrawer.saveCurrent()
        }
    }
}
*///?}