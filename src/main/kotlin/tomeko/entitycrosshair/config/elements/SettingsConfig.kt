package tomeko.entitycrosshair.config.elements

import cc.polyfrost.oneconfig.config.annotations.Switch
import tomeko.entitycrosshair.config.EntityCrosshairConfig;

class SettingsConfig {
    @Switch(name = "Show in F3 (Debug)", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showInDebug = false

    @Switch(name = "Show in GUIs", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showInGuis = true

    @Switch(name = "Show in Third Person", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showInThirdPerson = true

    @Switch(name = "Show in Spectator Mode", category = EntityCrosshairConfig.CATEGORY_SETTINGS)
    var showInSpectator = true
}