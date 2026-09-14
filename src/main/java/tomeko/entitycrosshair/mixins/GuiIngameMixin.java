package tomeko.entitycrosshair.mixins;

//? if 1.8.9 {
/*import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tomeko.entitycrosshair.config.EntityCrosshairConfig;
//? if forge {
//import tomeko.entitycrosshair.config.SettingsConfig;
//?}

@Mixin(GuiIngame.class)
public class GuiIngameMixin {
    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void entitycrosshair$check(CallbackInfoReturnable<Boolean> cir) {
        //? if forge {
        //SettingsConfig config = EntityCrosshairConfig.INSTANCE.getSettingsConfig();
        //?} else {
        EntityCrosshairConfig config = EntityCrosshairConfig.INSTANCE;
        //?}
        Minecraft mc = Minecraft.getMinecraft();

        if ((!config.getShowInGuis() && mc.currentScreen != null) || (!config.getShowInThirdPerson() && mc.gameSettings.thirdPersonView != 0)) {
            cir.setReturnValue(false);
        }
        if ((config.getShowInSpectator() && mc.playerController.isSpectator()) || (config.getShowWith3DCrosshair() && mc.gameSettings.showDebugInfo)) {
            cir.setReturnValue(true);
        }

        //? if ornithe {
        //cir.setReturnValue(false);
        //?}
    }
}
*///?}