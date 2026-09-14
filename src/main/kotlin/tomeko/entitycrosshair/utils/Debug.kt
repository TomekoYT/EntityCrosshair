package tomeko.entitycrosshair.utils

//? if fabric {
import org.slf4j.Logger
import org.slf4j.LoggerFactory
//?}
import tomeko.entitycrosshair.config.EntityCrosshairConfig

object Debug {
    //? if fabric {
    private val LOGGER: Logger = LoggerFactory.getLogger(Constants.MOD_ID)
    //?}

    fun log(message: String) {
        val config =
            //? if forge {
            //EntityCrosshairConfig.settingsConfig
            //?} else {
            EntityCrosshairConfig
        //?}
        if (!config.debugModeEnabled) return

        forceLog(message)
    }

    fun forceLog(message: String) {
        //? if forge {
        //println("[${Constants.MOD_NAME}] $message")
        //?} else {
        LOGGER.info("[${Constants.MOD_NAME}] $message")
        //?}
    }
}