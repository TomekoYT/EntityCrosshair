package tomeko.entitycrosshair.config

import cc.polyfrost.oneconfig.config.annotations.Switch

class RenderConfig {
    @Switch(name = "Show in F3 (Debug)")
    var showInDebug = false

    @Switch(name = "Show in GUIs")
    var showInGuis = true

    @Switch(name = "Show in Third Person")
    var showInThirdPerson = true

    @Switch(name = "Show in Spectator Mode")
    var showInSpectator = false
}