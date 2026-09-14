package tomeko.entitycrosshair

//? if forge {
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
//? if forge {
//import tomeko.entitycrosshair.config.base.*
//?}
import tomeko.entitycrosshair.utils.*

//? if forge {
/*@Mod(
    name = Constants.MOD_NAME,
    modid = Constants.MOD_ID,
    version = Constants.MOD_VERSION,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
*///?}
class EntityCrosshair
//? if fabric {
    : ClientModInitializer
//?}
{
    //? if forge {
    //@Mod.EventHandler
    //?} else {
    override
    //?}
    fun onInitializeClient(
        //? if forge {
        //event: FMLInitializationEvent
        //?}
    ) {
        //? if forge {
        //clearCaches()
        //?} else {
        Constants.CACHES_FILE.deleteRecursively()
        //?}
        Constants.CACHES_FILE.mkdirs()

        //? if fabric {
        System.setProperty("java.awt.headless", "false")
        //?}

        //? if forge {
        //EventManager.INSTANCE.register(this)
        //?}

        EntityCrosshairCommand.register()

        //? if fabric {
        CrosshairRenderer.register()
        //?}
        EntityCrosshairConfig.register()

        //? if fabric {
        ClientLifecycleEvents.CLIENT_STOPPING.register {
            Constants.CACHES_FILE.deleteRecursively()
        }
        //?}

        Debug.forceLog("${Constants.MOD_VERSION} Initialized!")
    }

    //? if forge {
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