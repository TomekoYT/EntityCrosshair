package tomeko.entitycrosshair.config.elements.general

import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.config.elements.base.BaseCrosshairEntry
import tomeko.entitycrosshair.utils.saveDefault

class GeneralCrosshairEntry(
    override var img: String = "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAAEUlEQVR42mNgGAWjYBQMIgAAA5MAAecADfkAAAAASUVORK5CYII\u003d",
) : BaseCrosshairEntry {
    @Slider(name = "Scale %", min = 0f, max = 200f, category = EntityCrosshairConfig.CATEGORY_GENERAL)
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
            val img = EntityCrosshairConfig.generalCanvaConfig.newCurrentCrosshair.img
            EntityCrosshairConfig.generalCanvaConfig.newCurrentCrosshair.loadFrom(GeneralCrosshairEntry())
            EntityCrosshairConfig.generalCanvaConfig.newCurrentCrosshair.img = img
            saveDefault(GeneralDrawer.saveFromDrawer(false))
        }
    }
}