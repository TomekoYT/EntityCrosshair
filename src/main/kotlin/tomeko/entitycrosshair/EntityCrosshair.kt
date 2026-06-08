package tomeko.entitycrosshair

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ShutdownEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import tomeko.entitycrosshair.render.CrosshairRenderer
import tomeko.entitycrosshair.utils.Constants

@Mod(
    name = Constants.MOD_NAME,
    modid = Constants.MOD_ID,
    version = Constants.MOD_VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
object EntityCrosshair {
    @Mod.EventHandler
    fun onInitializeClient(event: FMLInitializationEvent) {
        clearCaches()
        Constants.CACHES_FILE.mkdirs()

        EventManager.INSTANCE.register(this)
    }

    @Mod.EventHandler
    fun onPostInitializeClient(event: FMLPostInitializationEvent) {
        CrosshairRenderer.updateVanilla()
    }

    @Subscribe
    fun onShutDown(e: ShutdownEvent) {
        clearCaches()
    }

    fun clearCaches() {
        if (Constants.CACHES_FILE.listFiles()?.isNotEmpty() == true) {
            for (file in Constants.CACHES_FILE.listFiles()!!) {
                file.delete()
            }
        }
        Constants.CACHES_FILE.delete()
    }
}