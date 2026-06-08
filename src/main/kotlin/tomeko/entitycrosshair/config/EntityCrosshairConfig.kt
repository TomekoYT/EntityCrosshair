@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.*
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.config.elements.*
import tomeko.entitycrosshair.config.elements.*
import tomeko.entitycrosshair.utils.Constants
import tomeko.entitycrosshair.utils.indexToPos
import java.lang.reflect.Field
import kotlin.collections.iterator

object EntityCrosshairConfig : Config(
    Mod(Constants.MOD_NAME, ModType.HUD, "/assets/${Constants.MOD_ID}/icon.png"),
    "${Constants.MOD_ID}/config.json"
) {
    @Exclude
    private const val CATEGORY_GENERAL = "General"

    @Switch(name = "Show in F3 (Debug)", category = CATEGORY_GENERAL)
    var showInDebug = false

    @Switch(name = "Show in GUIs", category = CATEGORY_GENERAL)
    var showInGuis = true

    @Switch(name = "Show in Third Person", category = CATEGORY_GENERAL)
    var showInThirdPerson = true

    @Switch(name = "Show in Spectator Mode", category = CATEGORY_GENERAL)
    var showInSpectator = true

    @Exclude
    const val CATEGORY_DEFAULT = "Default"

    var defaultCanvaConfig = DefaultCanvaConfig()

    @Exclude
    const val CATEGORY_ENTITY = "Entity"

    var entityCanvaConfig = EntityCanvaConfig()

    init {
        initialize()

        this.generateOptionList(defaultCanvaConfig.newCurrentCrosshair, mod.defaultPage, this.mod, false)
        this.generateOptionList(defaultCanvaConfig, mod.defaultPage, this.mod, false)
        addListener("defaultCanvaConfig.canvaSize") {
            for (i in defaultCanvaConfig.drawerMap) {
                val pos = indexToPos(i.key)
                if (pos.x >= defaultCanvaConfig.canvaSize || pos.y >= defaultCanvaConfig.canvaSize) {
                    DefaultDrawer.pixels[i.key].isToggled = false
                }
            }
        }

        this.generateOptionList(entityCanvaConfig.newCurrentCrosshair, mod.defaultPage, this.mod, false)
        this.generateOptionList(entityCanvaConfig, mod.defaultPage, this.mod, false)
        addListener("entityCanvaConfig.canvaSize") {
            for (i in entityCanvaConfig.drawerMap) {
                val pos = indexToPos(i.key)
                if (pos.x >= entityCanvaConfig.canvaSize || pos.y >= entityCanvaConfig.canvaSize) {
                    EntityDrawer.pixels[i.key].isToggled = false
                }
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
                ConfigUtils.getSubCategory(page, CATEGORY_DEFAULT, "").options.add(DefaultDrawer)
            }
        }
        return null
    }

}