package tomeko.entitycrosshair

//? if = 1.8.9 {
/*import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.ShutdownEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
*///?} else {
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
//?}
import tomeko.entitycrosshair.commands.EntityCrosshairCommand
import tomeko.entitycrosshair.config.CrosshairRenderer
import tomeko.entitycrosshair.config.EntityCrosshairConfig
//? if = 1.8.9 {
/*import tomeko.entitycrosshair.config.base.*
*///?}
import tomeko.entitycrosshair.utils.Constants

//? if = 1.8.9 {
/*@Mod(
    name = Constants.MOD_NAME,
    modid = Constants.MOD_ID,
    version = Constants.MOD_VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
*///?}
class EntityCrosshair
//? if >= 1.21.11 {
    : ClientModInitializer
//?}
{
    //? if = 1.8.9 {
    /*@Mod.EventHandler
    *///?} else {
    override
    //?}
    fun onInitializeClient(
        //? if = 1.8.9 {
        /*event: FMLInitializationEvent
        *///?}
    ) {
        //? if = 1.8.9 {
        /*clearCaches()
        *///?} else {
        Constants.CACHES_FILE.deleteRecursively()
        //?}
        Constants.CACHES_FILE.mkdirs()

        //? if >= 1.21.11 {
        if (!System.getProperty("os.name").lowercase().contains("mac"))
            System.setProperty("java.awt.headless", "false")
        //?}

        //? if = 1.8.9 {
        /*EventManager.INSTANCE.register(this)
        *///?}

        EntityCrosshairCommand.register()

        //? if >= 1.21.11 {
        CrosshairRenderer.register()
        //?}
        EntityCrosshairConfig.register()

        //? if >= 1.21.11 {
        ClientLifecycleEvents.CLIENT_STOPPING.register {
            Constants.CACHES_FILE.deleteRecursively()
        }
        //?}
    }

    //? if = 1.8.9 {
    /*@Mod.EventHandler
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
    *///?}
}