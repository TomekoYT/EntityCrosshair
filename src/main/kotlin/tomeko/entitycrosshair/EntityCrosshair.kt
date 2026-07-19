package tomeko.entitycrosshair

import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ShutdownEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import tomeko.entitycrosshair.commands.*
import tomeko.entitycrosshair.config.*
import tomeko.entitycrosshair.config.base.*
import tomeko.entitycrosshair.utils.*

@Mod(
    name = Constants.MOD_NAME,
    modid = Constants.MOD_ID,
    version = Constants.MOD_VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
class EntityCrosshair {
    @Mod.EventHandler
    fun onInitializeClient(event: FMLInitializationEvent) {
        clearCaches()
        Constants.CACHES_FILE.mkdirs()

        EntityCrosshairCommand.register()

        EntityCrosshairConfig.register()

        EventManager.INSTANCE.register(this)
    }

    @Mod.EventHandler
    fun onPostInitializeClient(event: FMLPostInitializationEvent) {
        CrosshairRenderer.updateVanilla()
    }

    @Subscribe
    fun onShutDown(event: ShutdownEvent) {
        GeneralDrawer.flush()
        EntityDrawer.flush()
        EntityCrosshairConfig.save()

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