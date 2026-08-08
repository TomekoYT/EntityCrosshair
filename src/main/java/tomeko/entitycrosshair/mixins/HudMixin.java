package tomeko.entitycrosshair.mixins;

//? if >= 1.21.11 {

import net.minecraft.client.DeltaTracker;
//? if >= 26.1 {
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?} else {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
//? if >= 26.2 {
/*import net.minecraft.client.gui.Hud;
 *///?} else {
import net.minecraft.client.gui.Gui;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        //? if >= 26.2 {
        /*Hud.class
        *///?} else {
        Gui.class
        //?}
)
public abstract class HudMixin {
    @Inject(
            method =
                    //? if >= 26.1 {
                    "extractCrosshair",
                    //?} else {
                    /*"renderCrosshair",
            *///?}
            at = @At("HEAD"),
            cancellable = true
    )
    private void entitycrosshair$cancelVanillaCrosshair(
            //? if >= 26.1 {
            GuiGraphicsExtractor
            //?} else {
            /*GuiGraphics
                    *///?}
                    guiGraphics,
            DeltaTracker deltaTracker,
            CallbackInfo ci
    ) {
        ci.cancel();
    }
}
//?}