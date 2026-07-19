package tomeko.entitycrosshair.utils

import cc.polyfrost.oneconfig.config.core.ConfigUtils
import java.io.File

object Constants {
    const val MOD_NAME = "@MOD_NAME@"
    const val MOD_ID = "@MOD_ID@"
    const val MOD_VERSION = "@MOD_VERSION@"

    val CACHES_PATH = "${ConfigUtils.getProfileDir().absolutePath}/${MOD_ID}/caches/"
    val CACHES_FILE = File(CACHES_PATH)
}