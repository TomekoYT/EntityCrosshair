package tomeko.entitycrosshair.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void check(CallbackInfoReturnable<Boolean> cir) {
        EntityCrosshairConfig cfg = EntityCrosshairConfig.INSTANCE;
        Minecraft mc = Minecraft.getMinecraft();
        if (!EntityCrosshairConfig.INSTANCE.enabled) return;
        if ((!cfg.getShowInGuis() && mc.currentScreen != null) || (!cfg.getShowInThirdPerson() && mc.gameSettings.thirdPersonView != 0)) {
            cir.setReturnValue(false);
        }
        if ((cfg.getShowInSpectator() && mc.playerController.isSpectator()) || (cfg.getShowInDebug() && mc.gameSettings.showDebugInfo)) {
            cir.setReturnValue(true);
        }
    }
}