package tomeko.entitycrosshair.mixins;

//? if >= 26.1 {
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >= 26.2 {
/*import net.minecraft.client.gui.Hud;
*///?} else {
import net.minecraft.client.gui.Gui;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;

@Mixin(
        //? if >= 26.2 {
        /*Hud.class
        *///?} else {
        Gui.class
        //?}
)
public abstract class CancelCrosshairMixin {
    @Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
    private void entitycrosshair$cancelVanillaCrosshair(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (EntityCrosshairConfig.getEnabled()) {
            ci.cancel();
        }
    }
}
//?}