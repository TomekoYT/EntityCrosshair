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
import tomeko.entitycrosshair.commands.*
import tomeko.entitycrosshair.config.*
//? if = 1.8.9 {
/*import tomeko.entitycrosshair.config.base.*
*///?}
import tomeko.entitycrosshair.utils.*

//? if = 1.8.9 {
/*@Mod(
    name = Constants.MOD_NAME,
    modid = Constants.MOD_ID,
    version = Constants.MOD_VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
*///?}
class EntityCrosshair
//? if >= 26.1 {
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

        //? if = 1.8.9 {
        /*EventManager.INSTANCE.register(this)
        *///?}

        EntityCrosshairCommand.register()

        //? if >= 26.1 {
        CrosshairRenderer.register()
        //?}
        EntityCrosshairConfig.register()

        //? if >= 26.1 {
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