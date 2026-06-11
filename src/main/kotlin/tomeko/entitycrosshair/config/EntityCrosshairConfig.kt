@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.*
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.config.elements.*
import tomeko.entitycrosshair.config.elements.*
import tomeko.entitycrosshair.config.elements.entity.EntityCanvaConfig
import tomeko.entitycrosshair.config.elements.entity.EntityDrawer
import tomeko.entitycrosshair.config.elements.general.GeneralCanvaConfig
import tomeko.entitycrosshair.config.elements.general.GeneralDrawer
import tomeko.entitycrosshair.utils.Constants
import tomeko.entitycrosshair.utils.indexToPos
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

        addListener("generalCanvaConfig.canvaSize") {
            for (i in generalCanvaConfig.drawerMap) {
                val pos = indexToPos(i.key)
                if (pos.x >= generalCanvaConfig.canvaSize || pos.y >= generalCanvaConfig.canvaSize) {
                    GeneralDrawer.pixels[i.key].isToggled = false
                }
            }
        }

        this.generateOptionList(entityCanvaConfig, mod.defaultPage, this.mod, false)
        this.generateOptionList(entityCanvaConfig.newCurrentCrosshair, mod.defaultPage, this.mod, false)

        addListener("entityCanvaConfig.canvaSize") {
            for (i in entityCanvaConfig.drawerMap) {
                val pos = indexToPos(i.key)
                if (pos.x >= entityCanvaConfig.canvaSize || pos.y >= entityCanvaConfig.canvaSize) {
                    EntityDrawer.pixels[i.key].isToggled = false
                }
            }
        }

        this.generateOptionList(settingsConfig, mod.defaultPage, this.mod, false)
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