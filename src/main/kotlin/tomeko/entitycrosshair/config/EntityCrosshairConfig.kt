@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.*
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.config.elements.*
import tomeko.entitycrosshair.config.base.CanvaConfig
import tomeko.entitycrosshair.config.base.CrosshairDrawer
import tomeko.entitycrosshair.config.base.CrosshairEntry
import tomeko.entitycrosshair.config.base.EntityDrawer
import tomeko.entitycrosshair.config.base.GeneralDrawer
import tomeko.entitycrosshair.config.entity.EntityCanvaConfig
import tomeko.entitycrosshair.config.general.GeneralCanvaConfig
import tomeko.entitycrosshair.utils.Constants
import tomeko.entitycrosshair.utils.indexToPosition
import java.lang.reflect.Field

object EntityCrosshairConfig : Config(
    Mod(Constants.MOD_NAME, ModType.HUD, "/assets/${Constants.MOD_ID}/icon.png"),
    "${Constants.MOD_ID}/config.json"
) {
    @Exclude
    const val CATEGORY_GENERAL = "General"

    var generalCanvaConfig = GeneralCanvaConfig()

    @Exclude
    const val CATEGORY_ENTITY = "Entity"

    var entityCanvaConfig = EntityCanvaConfig()

    @Exclude
    const val CATEGORY_SETTINGS = "Settings"

    var settingsConfig = SettingsConfig()

    fun register() {
        initialize()

        this.generateOptionList(generalCanvaConfig, mod.defaultPage, this.mod, false)
        this.generateOptionList(generalCanvaConfig.newCurrentCrosshair, mod.defaultPage, this.mod, false)
        addListener("generalCanvaConfig.canvaSize") { clampOutOfBoundsPixels(generalCanvaConfig, GeneralDrawer) }

        this.generateOptionList(entityCanvaConfig, mod.defaultPage, this.mod, false)
        this.generateOptionList(entityCanvaConfig.newCurrentCrosshair, mod.defaultPage, this.mod, false)
        addListener("entityCanvaConfig.canvaSize") { clampOutOfBoundsPixels(entityCanvaConfig, EntityDrawer) }

        this.generateOptionList(settingsConfig, mod.defaultPage, this.mod, false)
    }

    private fun <T : CrosshairEntry> clampOutOfBoundsPixels(canvaConfig: CanvaConfig<T>, drawer: CrosshairDrawer<T>) {
        for ((key) in canvaConfig.drawerMap) {
            val pos = indexToPosition(key)
            if (pos.x >= canvaConfig.canvaSize || pos.y >= canvaConfig.canvaSize) {
                drawer.pixels[key].isToggled = false
            }
        }
    }

    override fun getCustomOption(
        field: Field,
        annotation: CustomOption,
        page: OptionPage,
        mod: Mod,
        migrate: Boolean,
    ): BasicOption? {
        when (annotation.id) {
            CATEGORY_ENTITY -> {
                ConfigUtils.getSubCategory(page, CATEGORY_ENTITY, "").options.add(EntityDrawer)
            }
            else -> {
                ConfigUtils.getSubCategory(page, CATEGORY_GENERAL, "").options.add(GeneralDrawer)
            }
        }
        return null
    }
}
