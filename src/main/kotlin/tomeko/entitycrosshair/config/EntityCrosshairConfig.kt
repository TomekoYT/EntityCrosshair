@file:Suppress("UnstableAPIUsage")

package tomeko.entitycrosshair.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.*
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.config.elements.*
import tomeko.entitycrosshair.utils.Constants
import tomeko.entitycrosshair.utils.indexToPos
import java.lang.reflect.Field
import kotlin.collections.iterator

object EntityCrosshairConfig : Config(Mod(Constants.MOD_NAME, ModType.HUD, "/assets/${Constants.MOD_ID}/icon.png"), "${Constants.MOD_ID}/config.json") {
    @Exclude
    var drawer = HashMap<Int, Int>()

    @CustomOption
    var newCrosshairs = arrayListOf(CrosshairEntry())

    var penColor = OneColor(-1)

    @Slider(
        name = "Canva Size",
        min = 15f, max = 32f
    )
    var canvaSize = 15
        get() = field.coerceIn(15, 32)

    var newCurrentCrosshair = CrosshairEntry()

    var renderConfig = RenderConfig()

    init {
        initialize()
        this.generateOptionList(newCurrentCrosshair, mod.defaultPage, this.mod, false)
        this.generateOptionList(renderConfig, mod.defaultPage, this.mod, false)
        addListener("canvaSize") {
            for (i in drawer) {
                val pos = indexToPos(i.key)
                if (pos.x >= canvaSize || pos.y >= canvaSize) {
                    Drawer.pixels[i.key].isToggled = false
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
        ConfigUtils.getSubCategory(page, "General", "").options.add(Drawer)
        return null
    }

}