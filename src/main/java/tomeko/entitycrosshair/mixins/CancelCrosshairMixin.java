package tomeko.entitycrosshair.mixins;

//? if >= 26.1 {
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;

@Mixin(Gui.class)
public abstract class CancelCrosshairMixin {
    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    private void entitycrosshair$cancelVanillaCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (EntityCrosshairConfig.getEnabled()) {
            ci.cancel();
        }
    }
}
//?}