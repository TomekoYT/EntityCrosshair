package tomeko.entitycrosshair.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin {

    @Inject(method = "renderCrosshairs", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelVanillaCrosshair(int width, int height, CallbackInfo ci) {
        if (EntityCrosshairConfig.INSTANCE.enabled) {
            ci.cancel();
            GlStateManager.enableAlpha();
        }
    }
}