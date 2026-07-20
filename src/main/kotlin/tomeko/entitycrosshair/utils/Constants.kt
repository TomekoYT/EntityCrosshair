package tomeko.entitycrosshair.utils

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.config.core.ConfigUtils
*///?} else {
import net.fabricmc.loader.api.FabricLoader
//?}
import java.io.File

object Constants {
    const val MOD_NAME = "@MOD_NAME@"
    const val MOD_ID = "@MOD_ID@"
    //? if = 1.8.9 {
    /*const val MOD_VERSION = "@MOD_VERSION@"
    *///?}

    const val MIN_CANVAS_SIZE = 15
    const val MAX_CANVAS_SIZE = 37

    //? if = 1.8.9 {
    /*val CACHES_PATH = "${ConfigUtils.getProfileDir().absolutePath}/${MOD_ID}/caches/"
    val CACHES_FILE = File(CACHES_PATH)
    *///?} else {
    val CONFIG_DIR: File = FabricLoader.getInstance().configDir.resolve(MOD_ID).toFile()
    val CACHES_FILE: File = File(CONFIG_DIR, "caches")
    val CACHES_PATH: String = CACHES_FILE.absolutePath + File.separator
    //?}
}