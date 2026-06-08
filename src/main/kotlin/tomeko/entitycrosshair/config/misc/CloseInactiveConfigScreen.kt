package tomeko.entitycrosshair.config.misc

import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.Display

object CloseInactiveConfigScreen {
    private val mc = Minecraft.getMinecraft()

    init {
        MinecraftForge.EVENT_BUS.register(CloseInactiveConfigScreen)
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (Display.isActive() || mc.currentScreen == null || !mc.currentScreen::class.java.name.contains("oneconfig")) return

        mc.displayGuiScreen(null)
    }
}