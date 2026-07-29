//? if = 1.8.9 {
/*@file:Suppress("UnstableAPIUsage")
*///?}

package tomeko.entitycrosshair.config

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.Config
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
*///?} else {
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import org.polyfrost.compose.render.PolyColor
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import tomeko.entitycrosshair.config.CrosshairRenderer.toPngBytes
//?}
import tomeko.entitycrosshair.utils.Constants
//? if = 1.8.9 {
/*import tomeko.entitycrosshair.utils.indexToPosition
import java.lang.reflect.Field
*///?} else {
import tomeko.entitycrosshair.utils.toBufferedImage

//?}

object EntityCrosshairConfig : Config(
    //? if = 1.8.9 {
    /*Mod(
        Constants.MOD_NAME,
        ModType.HUD,
        "/assets/${Constants.MOD_ID}/icon.png"
    ),
    "${Constants.MOD_ID}.json"
    *///?} else {
    "${Constants.MOD_ID}.json",
    "/assets/${Constants.MOD_ID}/icon.png",
    Constants.MOD_NAME,
    Category.HUD
    //?}
) {
    fun register() {
        //? if = 1.8.9 {
        /*initialize()

        this.generateOptionList(generalCanvaConfig, mod.defaultPage, this.mod, false)
        this.generateOptionList(generalCanvaConfig.newCurrentCrosshair, mod.defaultPage, this.mod, false)
        addListener("generalCanvaConfig.canvaSize") { clampOutOfBoundsPixels(generalCanvaConfig, GeneralDrawer) }

        this.generateOptionList(entityCanvaConfig, mod.defaultPage, this.mod, false)
        this.generateOptionList(entityCanvaConfig.newCurrentCrosshair, mod.defaultPage, this.mod, false)
        addListener("entityCanvaConfig.canvaSize") { clampOutOfBoundsPixels(entityCanvaConfig, EntityDrawer) }

        this.generateOptionList(settingsConfig, mod.defaultPage, this.mod, false)
        *///?} else {
        preload()
        clearPropertyLabels()

        generalSet = CrosshairSetData.decode(generalCrosshairJson, DefaultCrosshairs.GENERAL)
        entitySet = CrosshairSetData.decode(entityCrosshairJson, DefaultCrosshairs.ENTITY)
        pushTextures()

        addCallback<Boolean>("generalEraserEnabled") {
            generalEraserEnabledState = it
            false
        }

        addCallback<Boolean>("entityEraserEnabled") {
            entityEraserEnabledState = it
            false
        }
        //?}
    }

    //? if = 1.8.9 {
    /*@Exclude
    *///?}
    const val CATEGORY_GENERAL = "General"

    //? if = 1.8.9 {
    /*var generalCanvaConfig = GeneralCanvaConfig()
    *///?}

    //? if = 1.8.9 {
    /*@Exclude
    *///?}
    const val CATEGORY_ENTITY = "Entity"

    //? if = 1.8.9 {
    /*var entityCanvaConfig = EntityCanvaConfig()
    *///?}

    //? if = 1.8.9 {
    /*@Exclude
    *///?}
    const val CATEGORY_SETTINGS = "Settings"

    //? if = 1.8.9 {
    /*var settingsConfig = SettingsConfig()
    *///?}

    //? if >= 26.1 {
    @Slider(
        title = "Scale",
        min = 0f,
        max = 1000f,
        step = 1f,
        category = CATEGORY_GENERAL
    )
    var generalScale = 100f

    @Color(
        title = "Pen Color",
        category = CATEGORY_GENERAL
    )
    var generalColor = PolyColor(0xFFFFFFFF.toInt())

    @Switch(
        title = "Eraser",
        category = CATEGORY_GENERAL
    )
    var generalEraserEnabled = false

    var generalEraserEnabledState by mutableStateOf(generalEraserEnabled)
        private set

    @CrosshairEditor(category = CATEGORY_GENERAL, entityMode = false)
    var generalCrosshairJson: String = CrosshairSetData.default(DefaultCrosshairs.GENERAL).encode()


    @Slider(
        title = "Scale",
        min = 0f,
        max = 1000f,
        step = 1f,
        category = CATEGORY_ENTITY
    )
    var entityScale = 100f

    @Color(
        title = "Pen Color",
        category = CATEGORY_ENTITY
    )
    var entityColor = PolyColor(0xFFFFFFFF.toInt())

    @Switch(
        title = "Eraser",
        category = CATEGORY_ENTITY
    )
    var entityEraserEnabled = false

    var entityEraserEnabledState by mutableStateOf(entityEraserEnabled)
        private set

    @CrosshairEditor(category = CATEGORY_ENTITY, entityMode = true)
    var entityCrosshairJson: String = CrosshairSetData.default(DefaultCrosshairs.ENTITY).encode()

    @Switch(title = "Show with Minecraft's 3D Crosshair", category = CATEGORY_SETTINGS)
    var showWith3DCrosshair = false

    @Switch(title = "Show in GUIs", category = CATEGORY_SETTINGS)
    var showInGuis = true

    @Switch(title = "Show in Third Person", category = CATEGORY_SETTINGS)
    var showInThirdPerson = false

    @Switch(title = "Show in Spectator Mode", category = CATEGORY_SETTINGS)
    var showInSpectator = true

    var generalSet: CrosshairSetData = CrosshairSetData.default(DefaultCrosshairs.GENERAL)
        private set

    var entitySet: CrosshairSetData = CrosshairSetData.default(DefaultCrosshairs.ENTITY)
        private set

    //?}

    //? if = 1.8.9 {
    /*private fun <T : CrosshairEntry> clampOutOfBoundsPixels(canvaConfig: CanvaConfig<T>, drawer: CrosshairDrawer<T>) {
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
    *///?} else {
    fun onGeneralChanged(newSet: CrosshairSetData) {
        generalSet = newSet
        generalCrosshairJson = newSet.encode()
        save()
        pushGeneralTexture()
    }

    fun onEntityChanged(newSet: CrosshairSetData) {
        entitySet = newSet
        entityCrosshairJson = newSet.encode()
        save()
        pushEntityTexture()
    }

    private fun pushTextures() {
        ClientLifecycleEvents.CLIENT_STARTED.register { _ ->
            pushGeneralTexture()
            pushEntityTexture()
        }
    }

    private fun pushGeneralTexture() {
        toBufferedImage(generalSet.current.img)?.let { CrosshairRenderer.updateDefaultTexture(it.toPngBytes()) }
    }

    private fun pushEntityTexture() {
        toBufferedImage(entitySet.current.img)?.let { CrosshairRenderer.updateEntityTexture(it.toPngBytes()) }
    }

    private fun clearPropertyLabels() {
        try {
            var clazz: Class<*>? = this.javaClass
            while (clazz != null) {
                for (field in clazz.declaredFields) {
                    runCatching {
                        field.isAccessible = true
                        val value = field.get(this)
                        if (value != null) scanAndClean(value)
                    }
                }
                clazz = clazz.superclass
            }
        } catch (_: Throwable) {
        }
    }

    private fun scanAndClean(obj: Any) {
        val objClass = obj.javaClass
        val name = objClass.name

        if (obj is Map<*, *>) {
            obj.values.forEach { if (it != null) scanAndClean(it) }
            return
        }
        if (obj is Collection<*>) {
            obj.forEach { if (it != null) scanAndClean(it) }
            return
        }

        if (name.contains("polyfrost")) {
            runCatching {
                var c: Class<*>? = objClass
                var isTarget = false
                while (c != null) {
                    try {
                        val idField = c.getDeclaredField("id")
                        idField.isAccessible = true
                        val id = idField.get(obj)?.toString()
                        if (id == "generalCrosshairJson" || id == "entityCrosshairJson") {
                            isTarget = true
                            break
                        }
                    } catch (_: Exception) {
                    }
                    c = c.superclass
                }

                if (isTarget) {
                    var currentClass: Class<*>? = objClass
                    while (currentClass != null) {
                        for (fieldName in listOf("title", "name", "label", "description")) {
                            try {
                                val f = currentClass.getDeclaredField(fieldName)
                                f.isAccessible = true
                                f.set(obj, "")
                            } catch (_: Exception) {
                            }
                        }
                        currentClass = currentClass.superclass
                    }
                }

                var scanClass: Class<*>? = objClass
                while (scanClass != null) {
                    for (field in scanClass.declaredFields) {
                        if (java.lang.reflect.Modifier.isStatic(field.modifiers)) continue
                        try {
                            field.isAccessible = true
                            val fieldVal = field.get(obj)
                            if (fieldVal != null && fieldVal !== obj && !field.type.isPrimitive) {
                                scanAndClean(fieldVal)
                            }
                        } catch (_: Exception) {
                        }
                    }
                    scanClass = scanClass.superclass
                }
            }
        }
    }
    //?}
}
