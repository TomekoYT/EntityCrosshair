package tomeko.entitycrosshair.commands

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import tomeko.entitycrosshair.config.EntityCrosshairConfig
import tomeko.entitycrosshair.utils.Constants

@Command(value = Constants.MOD_ID)
object EntityCrosshairCommand {
    fun register() {
        CommandManager.INSTANCE.registerCommand(this)
    }

    @Main
    fun handle() {
        EntityCrosshairConfig.openGui()
    }
}