package tomeko.entitycrosshair.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;
import tomeko.entitycrosshair.config.SettingsConfig;

@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void entitycrosshair$check(CallbackInfoReturnable<Boolean> cir) {
        SettingsConfig cfg = EntityCrosshairConfig.INSTANCE.getSettingsConfig();
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