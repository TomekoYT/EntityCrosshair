package tomeko.entitycrosshair.config.elements

import cc.polyfrost.oneconfig.config.annotations.Button
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.utils.dsl.runAsync
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.utils.saveDefault

class DefaultCrosshairEntry(
    var img: String = "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAPCAYAAAA71pVKAAAAEUlEQVR42mNgGAWjYBQMIgAAA5MAAecADfkAAAAASUVORK5CYII\u003d",
) {
    @Slider(name = "Scale %", min = 0f, max = 200f, category = EntityCrosshairConfig.CATEGORY_DEFAULT)
    var scale = 100

    @Slider(name = "Rotation", min = -180f, max = 180f, category = EntityCrosshairConfig.CATEGORY_DEFAULT)
    var rotation = 0

    @Slider(name = "X Offset", min = -1920f, max = 1920f, category = EntityCrosshairConfig.CATEGORY_DEFAULT)
    var offsetX = 0

    @Slider(name = "Y Offset", min = -1080f, max = 1080f, category = EntityCrosshairConfig.CATEGORY_DEFAULT)
    var offsetY = 0

    @Switch(
        name = "Centered",
        description = "In vanilla Minecraft, the crosshair is not centered. Enable this option to center the crosshair.",
        category = EntityCrosshairConfig.CATEGORY_DEFAULT
    )
    var centered = false

    @Button(name = "Transform", text = "Reset", size = 1, category = EntityCrosshairConfig.CATEGORY_DEFAULT)
    var transformReset = Runnable {
        runAsync {
            val img = EntityCrosshairConfig.defaultCanvaConfig.newCurrentCrosshair.img
            EntityCrosshairConfig.defaultCanvaConfig.newCurrentCrosshair.loadFrom(DefaultCrosshairEntry())
            EntityCrosshairConfig.defaultCanvaConfig.newCurrentCrosshair.img = img
            saveDefault(DefaultDrawer.saveFromDrawer(false))
        }
    }

    fun loadFrom(entry: DefaultCrosshairEntry) {
        val newFields = ConfigUtils.getClassFields(entry::class.java)
        val fields = ConfigUtils.getClassFields(this::class.java)
        for (i in 0..<fields.size) {
            fields[i].set(this, ConfigUtils.getField(newFields[i], entry))
        }
    }
}