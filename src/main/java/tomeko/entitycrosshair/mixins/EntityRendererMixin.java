package tomeko.entitycrosshair.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;
import tomeko.entitycrosshair.render.CrosshairRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
    private void draw(float partialTicks, long nanoTime, CallbackInfo ci) {
        if (EntityCrosshairConfig.INSTANCE.enabled) CrosshairRenderer.INSTANCE.drawCrosshair((EntityRenderer) (Object) this);
    }
}