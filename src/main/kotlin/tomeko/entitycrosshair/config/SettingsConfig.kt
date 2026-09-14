package tomeko.entitycrosshair.config

//? if forge {
/*import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.InfoType

class SettingsConfig {
    @Switch(name = "Enabled", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var enabled = true

    @Switch(name = "Show with Minecraft's 3D Crosshair", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showWith3DCrosshair = false

    @Switch(name = "Show in GUIs", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showInGuis = true

    @Switch(name = "Show in Third Person", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showInThirdPerson = true

    @Switch(name = "Show in Spectator Mode", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showInSpectator = true

    @Info(text = "Probably should stay disabled", type = InfoType.WARNING, category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var debugModeInfo: Nothing? = null

    @Switch(name = "Debug Mode", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var debugModeEnabled = false
}
*///?}