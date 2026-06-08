package tomeko.entitycrosshair.config.elements

import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.utils.saveEntity

class EntityCrosshairEntry(
    var img: String = "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAAEUlEQVR42mNgGAWjYBQMIgAAA5MAAecADfkAAAAASUVORK5CYII\u003d",
) {
    @Slider(name = "Scale %", min = 0f, max = 200f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    var scale = 100

    @Slider(name = "Rotation", min = -180f, max = 180f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    var rotation = 0

    @Slider(name = "X Offset", min = -1920f, max = 1920f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    var offsetX = 0

    @Slider(name = "Y Offset", min = -1080f, max = 1080f, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    var offsetY = 0

    @Switch(
        name = "Centered",
        description = "In vanilla Minecraft, the crosshair is not centered. Enable this option to center the crosshair.",
        category = EntityCrosshairConfig.CATEGORY_ENTITY
    )
    var centered = true

    @Button(name = "Transform", text = "Reset", size = 1, category = EntityCrosshairConfig.CATEGORY_ENTITY)
    var transformReset = Runnable {
        runAsync {
            val img = EntityCrosshairConfig.entityCanvaConfig.newCurrentCrosshair.img
            EntityCrosshairConfig.entityCanvaConfig.newCurrentCrosshair.loadFrom(EntityCrosshairEntry())
            EntityCrosshairConfig.entityCanvaConfig.newCurrentCrosshair.img = img
            saveEntity(EntityDrawer.saveFromDrawer(false))
        }
    }

    fun loadFrom(entry: EntityCrosshairEntry) {
        this.img = entry.img
        this.scale = entry.scale
        this.rotation = entry.rotation
        this.offsetX = entry.offsetX
        this.offsetY = entry.offsetY
        this.centered = entry.centered
        this.transformReset = entry.transformReset
    }
}