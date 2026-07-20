package tomeko.entitycrosshair.mixins;

//? if = 1.8.9 {
/*import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin {
    @Inject(method = "renderCrosshairs", at = @At("HEAD"), cancellable = true, remap = false)
    private void entitycrosshair$cancelVanillaCrosshair(int width, int height, CallbackInfo ci) {
        if (!EntityCrosshairConfig.INSTANCE.getSettingsConfig().getEnabled()) return;

        ci.cancel();
        GlStateManager.enableAlpha();
    }
}
*///?}