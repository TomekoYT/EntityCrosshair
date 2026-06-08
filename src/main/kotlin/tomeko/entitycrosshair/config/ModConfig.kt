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

object ModConfig : Config(Mod(Constants.MOD_NAME, ModType.HUD, "/assets/${Constants.MOD_ID}/icon.png"), "${Constants.MOD_ID}/config.json") {

    @Exclude
    var drawer = HashMap<Int, Int>()

    @DualOption(
        name = "Mode",
        left = "Vanilla",
        right = "Custom",
        size = 2
    )
    var mode = false

    @CustomOption
    var newCrosshairs = arrayListOf(CrosshairEntry())

    var penColor = OneColor(-1)

    @Dropdown(
        name = "Mirror",
        options = ["Off", "Horizontal", "Vertical", "Quadrant"]
    )
    var mirror = 0

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
        var options = listOf("hostile", "passive", "player", "hostileColor", "passiveColor", "playerColor", "dynamicOpacity")
        for (i in options) {
            hideIf(i) { !renderConfig.dynamicColor }
        }
        addDependency(options[3], options[0])
        addDependency(options[4], options[1])
        addDependency(options[5], options[2])
        addDependency("centered", "mode")
        options = listOf("mirror", "canvaSize")
        options.forEach { hideIf(it) { !mode } }
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
        Drawer.addHideCondition { !mode }
        ConfigUtils.getSubCategory(page, "General", "").options.add(Drawer)
        return null
    }

}